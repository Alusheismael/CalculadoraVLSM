package com.alejo.calculadoravlsm;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText etIpAddress, etSubnetMask, etSubnets;
    private Button btnCalculate, btnRegresar;
    private TextView tvResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etIpAddress = findViewById(R.id.etIpAddress);
        etSubnetMask = findViewById(R.id.etSubnetMask);
        etSubnets = findViewById(R.id.etSubnets);
        btnCalculate = findViewById(R.id.btnCalculate);
        btnRegresar = findViewById(R.id.btnRegresar);
        tvResults = findViewById(R.id.tvResults);

        btnCalculate.setOnClickListener(view -> {
            String ip = etIpAddress.getText().toString().trim();
            String mask = etSubnetMask.getText().toString().trim();
            String subnetsStr = etSubnets.getText().toString().trim();

            if (TextUtils.isEmpty(ip) || TextUtils.isEmpty(mask) || TextUtils.isEmpty(subnetsStr)) {
                Toast.makeText(this, "Todos los campos son obligatorios.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isValidIp(ip)) {
                Toast.makeText(this, "Dirección IP inválida.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isValidIp(mask)) {
                Toast.makeText(this, "Máscara de subred inválida.", Toast.LENGTH_SHORT).show();
                return;
            }

            int subnets;
            try {
                subnets = Integer.parseInt(subnetsStr);
                if (subnets <= 0) {
                    Toast.makeText(this, "Debe ingresar un número positivo de subredes.", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Número de subredes inválido.", Toast.LENGTH_SHORT).show();
                return;
            }

            VLSMCalculator calculator = new VLSMCalculator(ip, mask, subnets);
            String result = calculator.calculate();
            tvResults.setText(result);
        });

        btnRegresar.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, MenuActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private boolean isValidIp(String ip) {
        String[] parts = ip.trim().split("\\.");
        if (parts.length != 4) return false;
        for (String part : parts) {
            try {
                int num = Integer.parseInt(part);
                if (num < 0 || num > 255) return false;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return true;
    }
}

