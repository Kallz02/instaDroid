package com.example.instaandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {


    private EditText inputUsername;
    private Button btnCheckStatus;
    private TextView tvResult;
    private LottieAnimationView loadingAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        inputUsername = findViewById(R.id.input_username);
        btnCheckStatus = findViewById(R.id.btn_check_status);
        tvResult = findViewById(R.id.tv_text);
        loadingAnimation = findViewById(R.id.loading_animation);
        hideLoadingAnimation();
        btnCheckStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = inputUsername.getText().toString();
                showLoadingAnimation();
                tvResult.setText("Loading......");
                checkStatus(username);
            }
        });
        //Gihub redirect
        ImageView myImage = findViewById(R.id.my_image);
        myImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the click event here
                // You can use an Intent to open a URL or any other action
                // For example, to open a URL:
                Uri uri = Uri.parse("https://github.com/Kallz02/InstaFakeDetectAPI");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
    }

    private void checkStatus(String username) {
        OkHttpClient client = new OkHttpClient();

        showLoadingAnimation();
//        tvResult.setText("Real account");
        String url = "https://instapi.akshayk.dev/user/" + username;

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                // Handle error
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvResult.setText("Server error");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String jsonResponse = response.body().string();
                try {
                    JSONObject jsonObject = new JSONObject(jsonResponse);
                    final int prediction = jsonObject.getInt("prediction");
                    hideLoadingAnimation();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (prediction == 0) {
                                tvResult.setText("Real account");
                            } else if (prediction == 1) {
                                tvResult.setText("Fake account");
                            } else {
                                tvResult.setText("Invalid response");
                            }
                        }
                    });
                } catch (JSONException e) {
                    // Handle error
                    e.printStackTrace();
                    hideLoadingAnimation();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvResult.setText("Invalid response");
                        }
                    });
                }
            }
        });
    }

    private void showLoadingAnimation() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadingAnimation.setVisibility(View.VISIBLE);
                loadingAnimation.cancelAnimation();
            }
        });
    }

    private void hideLoadingAnimation() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadingAnimation.setVisibility(View.GONE);
                loadingAnimation.cancelAnimation();
            }
        });
    }
}