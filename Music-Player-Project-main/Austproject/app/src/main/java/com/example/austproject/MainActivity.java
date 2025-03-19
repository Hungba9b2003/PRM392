package com.example.austproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class MainActivity extends AppCompatActivity {

    private EditText Username, Password;
    private Button Register, Login;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Username = findViewById(R.id.etUsername);
        Password = findViewById(R.id.etPassword);
        Login = findViewById(R.id.btLogin);
        Register = findViewById(R.id.btRegister);

        db = FirebaseFirestore.getInstance();

        // Kiểm tra nếu user đã đăng nhập thì chuyển đến dashboard
        SharedPreferences sharedPreferences = getSharedPreferences("LibPref", Context.MODE_PRIVATE);
        if (sharedPreferences.contains("user_email")) {
            goToDashboard();
        }

        Login.setOnClickListener(v -> {
            String username = Username.getText().toString().trim();
            String password = Password.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // 🔥 Truy vấn Firestore theo email (không dùng document(username))
            db.collection("accounts")
                    .whereEqualTo("email", username)
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        if (!querySnapshot.isEmpty()) {
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                String storedPassword = document.getString("password");
                                if (storedPassword != null && storedPassword.equals(hashPassword(password))) {
                                    // 🔥 Lưu thông tin user vào SharedPreferences
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("user_email", username);
                                    editor.apply();

                                    Toast.makeText(MainActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                                    goToDashboard();
                                    return; // Dừng vòng lặp sau khi tìm thấy user
                                }
                            }
                            Toast.makeText(MainActivity.this, "Incorrect password", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(MainActivity.this, "Login failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("LOGIN_ERROR", e.getMessage());
                    });
        });

        Register.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, RegisterActivity.class)));
    }

    private void goToDashboard() {
        Intent intent = new Intent(MainActivity.this, PublicLibraryActivity.class);
        startActivity(intent);
        finish(); // Đóng MainActivity để tránh quay lại màn hình login khi nhấn back
    }

    // 🔒 Hàm băm mật khẩu (để trùng với cách hash khi đăng ký)
    private String hashPassword(String password) {
        return Integer.toHexString(password.hashCode()); // Nên dùng SHA-256 hoặc BCrypt để bảo mật hơn
    }
}
