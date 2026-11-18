package vn.ptit.quanlylichhen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

public final class LoginActivity extends Activity {
    private DatabaseHelper database;
    private EditText usernameInput;
    private EditText passwordInput;
    private TextView loginError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        database = new DatabaseHelper(this);
        usernameInput = findViewById(R.id.username);
        passwordInput = findViewById(R.id.password);
        loginError = findViewById(R.id.login_error);

        findViewById(R.id.login_button).setOnClickListener(view -> login());
        passwordInput.setOnEditorActionListener((view, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                login();
                return true;
            }
            return false;
        });
    }

    private void login() {
        String username = usernameInput.getText().toString().trim();
        String password = passwordInput.getText().toString();
        if (username.isEmpty() || password.trim().isEmpty()) {
            showError(R.string.login_required);
            return;
        }
        if (!database.checkLogin(username, password)) {
            showError(R.string.login_wrong);
            return;
        }
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void showError(int message) {
        loginError.setText(message);
        loginError.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        database.close();
        super.onDestroy();
    }
}
