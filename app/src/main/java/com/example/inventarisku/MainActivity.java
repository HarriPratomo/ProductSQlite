package com.example.inventarisku;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.example.inventarisku.adapter.ProductAdapter;
import com.example.inventarisku.database.ProductDb;
import com.example.inventarisku.model.ProductObject;
import com.example.inventarisku.utils.ProductItemClick;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ProductAdapter adapter;
    private List<ProductObject> productObjectList = new ArrayList<>();
    private RecyclerView recyclerView;
    private TextView emptyProduct;
    private static final int REQUEST_WRITE_PERMISSION = 786;
    ImageView photoproduct, img;
    Uri imguri = null;

    private ProductDb db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProductDialog(false, null, -1);
            }
        });

        init();
        toogleEmptyProduct();

        productObjectList.addAll(db.getAllProduct());
        adapter = new ProductAdapter(this, productObjectList);
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2, GridLayoutManager.VERTICAL, false));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(new ProductItemClick(this, recyclerView, new ProductItemClick.ClickListener() {
            @Override
            public void onClick(View view, int position) {

            }

            @Override
            public void onLongClick(View view, int position) {
                showActionDialog(position);
            }
        }));
        toogleEmptyProduct();
    }

    private void toogleEmptyProduct() {
        if (db.getProductCount() > 0) {
            emptyProduct.setVisibility(View.GONE);
        } else {
            emptyProduct.setVisibility(View.VISIBLE);
        }
    }

    public void init() {
        db = new ProductDb(this);
        recyclerView = findViewById(R.id.rv_main);
        emptyProduct = findViewById(R.id.empty_product);
    }

    private void createProduct(ProductObject object) {
        long id = db.insertProduct(object);
        ProductObject n = db.getProduct(id);
        if (n != null) {
            productObjectList.add(0, n);
            adapter.notifyDataSetChanged();
        }
        toogleEmptyProduct();
    }

    private void updateProduct(ProductObject product, int position) {
        ProductObject p = productObjectList.get(position);
        p.setName(product.getName());
        p.setPrice(product.getPrice());
        p.setImg(product.getImg());

        db.updateProduct(p);
        productObjectList.set(position, p);
        adapter.notifyDataSetChanged();
    }

    private void deleteProduct(int position) {
        db.deleteProduct(productObjectList.get(position));
        productObjectList.remove(position);
        adapter.notifyDataSetChanged();
    }

    private void showActionDialog(final int position) {
        CharSequence colors[] = new CharSequence[]{"Edit Product", "Delete Product"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Option");
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    showProductDialog(true, productObjectList.get(position), position);
                } else {
                    deleteProduct(position);
                }
            }
        });
        builder.show();
    }

    private void showProductDialog(final boolean shouldUpdate, final ProductObject product, final int position) {
        LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
        View view = layoutInflater.inflate(R.layout.dialog_insert, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setView(view);

        photoproduct = view.findViewById(R.id.photoProduct);
        img = view.findViewById(R.id.img);

        final EditText nameProduct = view.findViewById(R.id.nameProduct);
        final EditText priceProduct = view.findViewById(R.id.priceProduct);


        photoproduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);
                } else {
                    CropImage.startPickImageActivity(MainActivity.this);
                }
            }
        });
        TextView dialogTitle = view.findViewById(R.id.dialog_title);
        dialogTitle.setText(!shouldUpdate ? "Add New Product " : "Edit Product");
        if (shouldUpdate && product != null) {
            nameProduct.setText(product.getName());
            priceProduct.setText(product.getPrice());
        }
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton(shouldUpdate ? "Update" : " Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(nameProduct.getText().toString())) {
                    Toast.makeText(MainActivity.this, "Enter Product", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    alertDialog.dismiss();
                }

                ProductObject p = new ProductObject();
                p.setName(nameProduct.getText().toString());
                p.setPrice(priceProduct.getText().toString());
                p.setImg(imguri.toString());

                if (shouldUpdate) {
                    updateProduct(p, position);
                } else {
                    createProduct(p);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_WRITE_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            CropImage.startPickImageActivity(MainActivity.this);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint({"NewApi", "MissingSuperCall"})
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri imageUri = CropImage.getPickImageResultUri(this, data);
            if (CropImage.isReadExternalStoragePermissionsRequired(this, imageUri)) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            } else {
                Crop(imageUri);
            }
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                photoproduct.setImageURI(result.getUri());
                imguri = result.getUri();
                img.setVisibility(View.GONE);
            }
        }
    }

    private void Crop(Uri imageUri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setRequestedSize(500, 500)
                .setMultiTouchEnabled(true)
                .setAspectRatio(1, 1)
                .start(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
