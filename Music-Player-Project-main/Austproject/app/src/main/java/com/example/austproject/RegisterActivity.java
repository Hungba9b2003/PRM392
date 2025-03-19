package com.example.austproject;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.UUID;

public class RegisterActivity extends AppCompatActivity {

    private EditText Username, Password, ConfirmPassword;
    private Button Register, Back;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Username = findViewById(R.id.etUsername);
        Password = findViewById(R.id.etPassword);
        ConfirmPassword = findViewById(R.id.etConfirmPassword);
        Register = findViewById(R.id.btRegister);
        Back = findViewById(R.id.btBack);

        db = FirebaseFirestore.getInstance();

        Register.setOnClickListener(v -> {
            String username = Username.getText().toString().trim();
            String password = Password.getText().toString().trim();
            String confirmPassword = ConfirmPassword.getText().toString().trim();

            // Kiểm tra input hợp lệ
            if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                showToast("Please fill in all fields");
                return;
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(username).matches()) {
                showToast("Invalid email address");
                return;
            }
            if (password.length() < 6) {
                showToast("Password must be at least 6 characters");
                return;
            }
            if (!password.equals(confirmPassword)) {
                showToast("Passwords do not match!");
                return;
            }

            // Kiểm tra xem email đã tồn tại chưa
            db.collection("accounts")
                    .whereEqualTo("email", username)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            showToast("Email already registered");
                        } else {
                            registerUser(username, password);
                        }
                    })
                    .addOnFailureListener(e -> showToast("Error checking email: " + e.getMessage()));
        });

        Back.setOnClickListener(v -> finish());
    }

    private void registerUser(String username, String password) {
        String hashedPassword = hashPassword(password);
        String userId = UUID.randomUUID().toString();

        HashMap<String, Object> user = new HashMap<>();
        user.put("email", username);
        user.put("password", hashedPassword);
        user.put("createdAt", System.currentTimeMillis());

        db.collection("accounts").document(userId)
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    showToast("Registration Successful");
                    finish();
                })
                .addOnFailureListener(e -> showToast("Registration Failed: " + e.getMessage()));
    }

    // Hàm băm mật khẩu (nên dùng thư viện bảo mật như BCrypt hoặc SHA)
    private String hashPassword(String password) {
        return Integer.toHexString(password.hashCode()); // Không an toàn, chỉ để minh họa
    }

    private void showToast(String message) {
        Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}
