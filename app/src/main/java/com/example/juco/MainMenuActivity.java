package com.example.juco;

import android.annotation.SuppressLint;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import androidx.cardview.widget.CardView;
import android.widget.Button;
import android.view.View;

public class MainMenuActivity extends AppCompatActivity {
    private CardView clothingCard;
    private CardView messageCard;
    private CardView doCard;
    private CardView ayudaCard;
    private Button aboutUsButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        getSupportActionBar().hide();

        clothingCard = findViewById(R.id.clothingCard);
        doCard = findViewById(R.id.doCard);


        clothingCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMenuActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        doCard.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMenuActivity.this, VisualizarCotizacionesActivity.class);
                startActivity(intent);
            }
        });


    }
}
