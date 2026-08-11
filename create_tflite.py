import tensorflow as tf
import json

vocab = {"[PAD]": 0, "[UNK]": 1, "استرس": 2, "نگران": 3, "خسته": 4, "بد": 5, "خوب": 6, "عالی": 7}
with open("app/src/main/assets/vocab.json", "w", encoding="utf-8") as f:
    json.dump(vocab, f, ensure_ascii=False)

model = tf.keras.Sequential([
    tf.keras.layers.Input(shape=(100,)),
    tf.keras.layers.Dense(16, activation='relu'),
    tf.keras.layers.Dense(1, activation='sigmoid')
])
model.compile(optimizer='adam', loss='binary_crossentropy')

import numpy as np
X = np.random.rand(10, 100).astype(np.float32)
y = np.random.randint(2, size=(10, 1)).astype(np.float32)
model.fit(X, y, epochs=1)

converter = tf.lite.TFLiteConverter.from_keras_model(model)
tflite_model = converter.convert()

with open("app/src/main/assets/stress_model_quantized.tflite", "wb") as f:
    f.write(tflite_model)
print("TFLite model created!")
