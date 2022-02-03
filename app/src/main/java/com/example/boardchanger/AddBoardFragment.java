package com.example.boardchanger;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.ALARM_SERVICE;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.boardchanger.model.Board;
import com.example.boardchanger.model.Model;

import java.io.InputStream;


public class AddBoardFragment extends Fragment {

    EditText boardName;
    EditText boardPrice;
    EditText boardDesc;
    EditText boardAddress;
    EditText boardYear;
    Button addBoard;
    ProgressBar progressBar;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_IMAGE_SELECTION =2;
    Bitmap imageBitmap;
    ImageView boardP;
    ImageButton galleryBtn;
    ImageButton camBtn;

    public AddBoardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_board, container, false);
        boardName = view.findViewById(R.id.add_board_model);
        boardPrice = view.findViewById(R.id.add_board_price);
        boardDesc = view.findViewById(R.id.add_board_desc);
        boardAddress = view.findViewById(R.id.add_board_address);
        boardYear = view.findViewById(R.id.add_board_year);
        addBoard = view.findViewById(R.id.add_board_add_board_btn);
        progressBar = view.findViewById(R.id.add_board_progressbar);
        progressBar.setVisibility(View.GONE);
        boardP = view.findViewById(R.id.add_board_image_preview);



        addBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add();
            }
        });

        camBtn = view.findViewById(R.id.add_board_take_image_btn);
        galleryBtn = view.findViewById(R.id.add_board_add_image_btn);

        camBtn.setOnClickListener(v->{
            openCamera();
        });

        galleryBtn.setOnClickListener(v->{
            openGallery();
        });
        return view;
    }
    private void openGallery(){
        Intent openGalleryIntent = new Intent();
        openGalleryIntent.setType("image/*");
        openGalleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(openGalleryIntent,REQUEST_IMAGE_SELECTION);

    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent,REQUEST_IMAGE_CAPTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_IMAGE_CAPTURE){
            if(resultCode == Activity.RESULT_OK){
                Bundle extras = data.getExtras();
                imageBitmap = (Bitmap) extras.get("data");
                boardP.setImageBitmap(imageBitmap);
            }
        }else if(requestCode == REQUEST_IMAGE_SELECTION){
            if(resultCode == RESULT_OK){
               try{
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContext().getContentResolver().openInputStream(imageUri);
                imageBitmap = BitmapFactory.decodeStream(imageStream);
                boardP.setImageBitmap(imageBitmap);
            }catch(Exception e){
                   e.printStackTrace();
                   Toast.makeText(getActivity(),"Failed to select image",Toast.LENGTH_LONG).show();
               }
            }
        }

    }

    private void add(){
        progressBar.setVisibility(View.VISIBLE);
        if (boardYear.getText().toString().equals("")||boardDesc.getText().toString().equals("")||
                boardName.getText().toString().equals("")||boardPrice.getText().toString().equals("")||
                boardAddress.getText().toString().equals("")){
            Toast.makeText(getActivity(),"All Fields Must be Filled!",Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
                    return;
        }

        if(imageBitmap == null) {
            Toast.makeText(getActivity(), "You Must Add A Board Image!", Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
            return;
        }

        addBoard.setEnabled(false);
        camBtn.setEnabled(false);
        galleryBtn.setEnabled(false);

        String name = boardName.getText().toString();
        String price = boardPrice.getText().toString();
        String desc = boardDesc.getText().toString();
        String year = boardYear.getText().toString();
        String address = boardAddress.getText().toString();
        Board board = new Board(name,year,price, desc, address);
        Model.instance.saveImage(imageBitmap, name + ".jpg", url -> {
                board.setImageUrl(url);
                });
        Model.instance.addBoard(board,()->{
            Navigation.findNavController(boardName).navigateUp();
        });
    }
}