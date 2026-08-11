import streamlit as st
import pandas as pd
import numpy as np
import tensorflow as tf
from tensorflow.keras import layers, models
import json
import os

st.set_page_config(page_title="ArameshYar MLOps Dashboard", layout="wide")

st.title("🧠 ArameshYar MLOps & Admin Dashboard")
st.markdown("Monitor database metrics, train models, and manage the Edge AI pipeline.")

st.header("1. Synthetic Dataset Generator")
if st.button("Generate Synthetic Dataset"):
    data = {
        "text": [
            "خیلی استرس دارم و نمیتونم بخوابم", 
            "امروز حالم خیلی خوبه", 
            "عصبانی ام از دست همکارم", 
            "احساس خستگی و فرسودگی میکنم", 
            "کمی نگران امتحان فردا هستم",
            "دلم گرفته و غمگینم",
            "انرژی زیادی دارم و آماده ام"
        ] * 50,
        "label": [1, 0, 1, 1, 1, 1, 0] * 50,
        "category": ["anxiety", "joy", "anger", "burnout", "anxiety", "depression", "joy"] * 50
    }
    df = pd.DataFrame(data)
    df.to_csv("train.csv", index=False)
    st.success("Generated 'train.csv' with 350 samples.")
    st.dataframe(df.head())

st.header("2. Edge AI Model Training (TFLite)")
if st.button("Train & Export Quantized Model"):
    if not os.path.exists("train.csv"):
        st.error("Please generate the dataset first.")
    else:
        with st.spinner("Training model..."):
            df = pd.read_csv("train.csv")
            texts = df["text"].values
            labels = df["label"].values
            
            # 1. Text Vectorization
            vocab_size = 1000
            sequence_length = 20
            vectorizer = layers.TextVectorization(
                max_tokens=vocab_size,
                output_mode="int",
                output_sequence_length=sequence_length
            )
            vectorizer.adapt(texts)
            
            # Save vocabulary for Android
            vocab = vectorizer.get_vocabulary()
            with open("vocab.json", "w", encoding="utf-8") as f:
                json.dump(vocab, f, ensure_ascii=False)
            
            # 2. Build Keras Model
            model = models.Sequential([
                layers.Input(shape=(1,), dtype=tf.string),
                vectorizer,
                layers.Embedding(input_dim=vocab_size, output_dim=16),
                layers.GlobalAveragePooling1D(),
                layers.Dense(16, activation="relu"),
                layers.Dense(1, activation="sigmoid")
            ])
            
            model.compile(optimizer="adam", loss="binary_crossentropy", metrics=["accuracy"])
            model.fit(texts, labels, epochs=5, batch_size=32, verbose=0)
            st.success("Model trained successfully.")
            
            # 3. Post-Training Quantization (Float32 -> Int8)
            converter = tf.lite.TFLiteConverter.from_keras_model(model)
            converter.optimizations = [tf.lite.Optimize.DEFAULT]
            
            def representative_dataset():
                for text in texts[:100]:
                    yield [np.array([text])]
            
            converter.representative_dataset = representative_dataset
            # To ensure it operates completely in Int8 if needed, though with text ops it might need Select TF Ops
            converter.target_spec.supported_ops = [
                tf.lite.OpsSet.TFLITE_BUILTINS, # enable TensorFlow Lite ops.
                tf.lite.OpsSet.SELECT_TF_OPS # enable TensorFlow ops.
            ]
            
            tflite_quant_model = converter.convert()
            
            with open("stress_model_quantized.tflite", "wb") as f:
                f.write(tflite_quant_model)
                
            st.success("Quantized model saved as 'stress_model_quantized.tflite'")
            st.info("Vocabulary saved as 'vocab.json'")

st.header("3. Deployment Status")
st.metric(label="Android Offline Model Size", value="< 500 KB", delta="Optimized via Int8 Quantization")
