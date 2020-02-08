package com.example.ecom1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.ecom1.Model.Products;
import com.example.ecom1.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ProductsDetailsActivity extends AppCompatActivity {

    private ImageView productImage;
private ElegantNumberButton numberButton;
private TextView productPrice,productDescription,productName;
private Button addToCartButton;
    String productID;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_details);


       productID = getIntent().getStringExtra("pid");

        addToCartButton = findViewById(R.id.pd_add_to_cart);
        numberButton = findViewById(R.id.number_btn);
        productImage=findViewById(R.id.product_image_details);
        productName=findViewById(R.id.product_name_details);
        productDescription=findViewById(R.id.product_description_details);
        productPrice=findViewById(R.id.product_price_details);

        getProductDetails(productID);
        addToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                addingToCartList();
            }
        });

    }

    private void addingToCartList() {

        String saveCurrentTime, saveCurrentDate;

        Calendar callForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(callForDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss: a");
        saveCurrentTime = currentDate.format(callForDate.getTime());

         final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");

          final HashMap<String,Object>cartMap = new HashMap<>();
          cartMap.put("pid",productID);
          cartMap.put("pname",productName.getText().toString());
        cartMap.put("price",productPrice.getText().toString());
        cartMap.put("date",saveCurrentDate);
        cartMap.put("time",saveCurrentTime);
          cartMap.put("quantity",numberButton.getNumber());
          cartMap.put("discount","");

cartListRef.child("User View").child(Prevalent.currentOnlineUser.getPhone())
        .child("Products").child(productID).updateChildren(cartMap).
addOnCompleteListener(new OnCompleteListener<Void>() {
    @Override
    public void onComplete(@NonNull Task<Void> task) {
if (task.isSuccessful()){

    cartListRef.child("Admin View").child(Prevalent.currentOnlineUser.getPhone())
            .child("Products").child(productID).updateChildren(cartMap).
            addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful()){

                        Toast.makeText(ProductsDetailsActivity.this, "Added to cart List", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ProductsDetailsActivity.this,HomeActivity.class);
                  startActivity(intent);
                    }


                }
            });

}

    }
});

    }

    private void getProductDetails(String productID) {

        DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference().child("Products");

         productsRef.child(productID).addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                 if (dataSnapshot.exists()) {



                     Products products = dataSnapshot.getValue(Products.class);



                    productPrice.setText(products.getPrice());
                    productDescription.setText(products.getDescription());
                    productName.setText(products.getPname());
                    Picasso.get().load(products.getImage()).into(productImage);


                 }
             }
             @Override
             public void onCancelled(@NonNull DatabaseError databaseError) {

             }
         });

    }


}