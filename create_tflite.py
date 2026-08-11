import tensorflow as tf
import json
import os

# Create a tiny dummy vocabulary
vocab = {"استرس": 1, "نگران": 2, "خسته": 3, "بد": 4, "خوب": 5, "عالی": 6}
with open("app/src/main/assets/vocab.json", "w", encoding="utf-8") as f:
    json.dump(vocab, f, ensure_ascii=False)

# Create a tiny dummy TF model
model = tf.keras.Sequential([
    tf.keras.layers.Input(shape=(10,)),
    tf.keras.layers.Dense(6, activation='softmax')
])
model.compile(optimizer='adam', loss='sparse_categorical_crossentropy')

# Convert to TFLite
converter = tf.lite.TFLiteConverter.from_keras_model(model)
tflite_model = converter.convert()

with open("app/src/main/assets/stress_model_quantized.tflite", "wb") as f:
    f.write(tflite_model)
print("Created tflite and vocab")
