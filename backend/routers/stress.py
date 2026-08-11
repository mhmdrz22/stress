from fastapi import APIRouter, HTTPException, Body
from pydantic import BaseModel, Field
import httpx
import os
import json
import asyncio
from typing import List
import re

router = APIRouter(tags=["Stress Analysis"])

class ChatMessageSchema(BaseModel):
    role: str
    text: str

class MultiTurnAnalyzeRequest(BaseModel):
    device_id: str
    history: List[ChatMessageSchema]
    current_message: str

class AnalyzeResponse(BaseModel):
    has_stress: bool
    category_tag: str
    empathy_message: str
    search_keywords: List[str]

HF_TOKEN = os.getenv("HF_TOKEN", "")

SYSTEM_PROMPT = """You are an Empathetic Persian AI Assistant named "ArameshYar" (آرامش‌یار) specializing in mental health support.
Your task is to analyze the user's input along with the chat history.
Be warm, validate their emotions, and ALWAYS end your `empathy_message` with an open-ended question to encourage them to keep talking.
Never sound clinical.
Respond ONLY in valid JSON format matching this schema:
{
  "has_stress": bool,
  "category_tag": "anxiety" | "depression" | "anger" | "sleep" | "burnout" | "joy",
  "empathy_message": "string (Persian empathic response)",
  "search_keywords": ["keyword1", "keyword2"]
}"""

@router.post("/analyze-chat", response_model=AnalyzeResponse)
async def analyze_chat(request: MultiTurnAnalyzeRequest = Body(...)):
    if not request.current_message.strip():
        raise HTTPException(status_code=400, detail="Text cannot be empty")
        
    messages = [{"role": "system", "content": SYSTEM_PROMPT}]
    for msg in request.history:
        role = "assistant" if msg.role in ["agent", "model"] else "user"
        messages.append({"role": role, "content": msg.text})
    
    messages.append({"role": "user", "content": request.current_message})
        
    try:
        async with httpx.AsyncClient(timeout=30.0) as client:
            headers = {
                "Authorization": f"Bearer {HF_TOKEN}",
                "Content-Type": "application/json"
            }
            # Using Qwen2.5-7B-Instruct since it is highly performant for Persian logic, but increased timeout to 30s.
            # If rate limited, a smaller model could be used: Qwen/Qwen2.5-7B-Instruct
            response = await client.post(
                "https://api-inference.huggingface.co/models/Qwen/Qwen2.5-7B-Instruct/v1/chat/completions",
                headers=headers,
                json={
                    "model": "Qwen/Qwen2.5-7B-Instruct",
                    "messages": messages,
                    "temperature": 0.3,
                    "max_tokens": 800,
                    "response_format": {"type": "json_object"}
                }
            )
            
            if response.status_code != 200:
                raise HTTPException(status_code=response.status_code, detail=f"Hugging Face API Error: {response.text}")
                
            data = response.json()
            content = data["choices"][0]["message"]["content"]
            
            # Robust JSON extraction
            match = re.search(r'\{.*\}', content, re.DOTALL)
            if match:
                content = match.group(0)
            
            try:
                parsed = json.loads(content)
                return AnalyzeResponse(**parsed)
            except (json.JSONDecodeError, TypeError, ValueError) as e:
                raise HTTPException(status_code=500, detail="Invalid JSON format from AI model")
                
    except httpx.TimeoutException:
        raise HTTPException(status_code=504, detail="Gateway Timeout - The upstream AI provider did not respond in time.")
    except httpx.RequestError as exc:
        raise HTTPException(status_code=502, detail="An error occurred while requesting upstream API.")
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))
