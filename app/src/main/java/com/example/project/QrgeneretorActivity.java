package com.example.project;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.project.model.deta_class;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.ByteArrayOutputStream;

public class QrgeneretorActivity extends AppCompatActivity {
    private EditText nameEditText, ageEditText, mobileEditText, addressEditText, problemEditText, bloodGroupEditText, additionalInfoEditText;
    private RadioGroup genderRadioGroup;
    private RadioButton maleRadioButton, femaleRadioButton;
    private Button uploadImageButton, generateButton,showqr;
    FirebaseAuth auth =FirebaseAuth.getInstance();
    String name,phone,gender,addres,problem,bd_group,add_info,age,email=auth.getCurrentUser().getEmail();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_qrgeneretor);

        // Initialize views
        nameEditText = findViewById(R.id.name_edittext);
        ageEditText = findViewById(R.id.age_edittext_edittext);
        mobileEditText = findViewById(R.id.mobile_edittext);
        addressEditText = findViewById(R.id.address_edittext);
        problemEditText = findViewById(R.id.problem_edittext);
        bloodGroupEditText = findViewById(R.id.bloodgroup_edittext);
        additionalInfoEditText = findViewById(R.id.Aditionalinformation_edittext);
        genderRadioGroup = findViewById(R.id.gender_radiogroup);
        maleRadioButton = findViewById(R.id.male_radiobutton);
        femaleRadioButton = findViewById(R.id.female_radiobutton);
        uploadImageButton = findViewById(R.id.upload_image_button);
        generateButton = findViewById(R.id.Generate_buttot);
        showqr=findViewById(R.id.show_button);

        // Set listeners
        uploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Upload image logic here
                Toast.makeText(QrgeneretorActivity.this, "Upload image button clicked", Toast.LENGTH_SHORT).show();
            }
        });
        showqr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(QrgeneretorActivity.this, qrdetails.class);
                startActivity(intent);
            }
        });
        generateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Generate QR code logic here
//                Toast.makeText(QrgeneretorActivity.this, "Generate button clicked", Toast.LENGTH_SHORT).show();
                name=nameEditText.getText().toString();
                phone=mobileEditText.getText().toString();
                age=ageEditText.getText().toString();
                gender=((RadioButton)findViewById(genderRadioGroup.getCheckedRadioButtonId()))
                        .getText().toString();
                addres=addressEditText.getText().toString();
                problem=problemEditText.getText().toString();
                bd_group=bloodGroupEditText.getText().toString();
                add_info=additionalInfoEditText.getText().toString();


                if (name.isEmpty() && phone.isEmpty() && gender.isEmpty() && addres.isEmpty() && problem.isEmpty() && bd_group.isEmpty() && add_info.isEmpty()) {
                    Toast.makeText(QrgeneretorActivity.this, "All field are required", Toast.LENGTH_SHORT).show();
                }else {
                    deta_class dc =new deta_class(name,phone,gender,addres,problem,bd_group,add_info);
                    String data="name:"+name+"\nage:"+age+"\ngender:"+gender+"\naddress:"+addres+"\nproblem:"+problem+"\nBlood Group:"+bd_group+"\nAdditional Info:"+add_info;
                    final FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference ref = database.getReference("Doctor/"+name+"/Data");
                    ref.setValue(dc).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(QrgeneretorActivity.this, "Your Data Uploaded", Toast.LENGTH_SHORT).show();
                                gen_qr_code(data,email);
                            }
                        }
                    });
                    
                }

            }
        });



    }
    void gen_qr_code(String dc_1,String email){
        BarcodeEncoder encoder = new BarcodeEncoder();
        try {
            Bitmap bitmap = encoder.encodeBitmap(dc_1,BarcodeFormat.QR_CODE,400,400);
            StorageReference storageRef = FirebaseStorage.getInstance().getReference("User").child("Image");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageData = baos.toByteArray();
            UploadTask uploadTask = storageRef.putBytes(imageData);
            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(QrgeneretorActivity.this, "QR Code is Uploaded", Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(QrgeneretorActivity.this, qrdetails.class);
                        startActivity(intent);
                    }
                }
            });
        }catch (WriterException e){
            e.printStackTrace();
        }
    }
}