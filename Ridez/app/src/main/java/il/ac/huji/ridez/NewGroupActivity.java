package il.ac.huji.ridez;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import il.ac.huji.ridez.contentClasses.RidezGroup;


public class NewGroupActivity extends ActionBarActivity {
    private static int RESULT_GROUP_ICON = 1;
    private EditText groupName, groupDesc;
    private ImageButton buttonGroupProfilePicture;
    private String iconPath = "blank";
    ListView memberListView;
    ArrayList<String> members;
    ArrayAdapter<String> arrayAdapter;
    RidezGroup newGroup = new RidezGroup();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_new_group);
        groupName = (EditText) findViewById(R.id.edtTextGroupName);
        groupDesc = (EditText) findViewById(R.id.edtTextGroupDescription);
        buttonGroupProfilePicture = (ImageButton) findViewById(R.id.buttonGroupProfilePicture);
        ArrayList<String> emailAddressCollection = new ArrayList<>();

        ContentResolver cr = getContentResolver();

        Cursor emailCur = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, null, null, null);

        while (emailCur.moveToNext())
        {
            String email = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
            emailAddressCollection.add(email);
        }
        emailCur.close();

        String[] emailAddresses = new String[emailAddressCollection.size()];
        emailAddressCollection.toArray(emailAddresses);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, emailAddresses);
        final AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.searchGruopMembers);
        textView.setAdapter(adapter);
        Button addMember = (Button) findViewById(R.id.addMemberButton);
        addMember.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                final ProgressDialog pd = ProgressDialog.show(NewGroupActivity.this, "Please wait", "", true, false);
                final String emailText = textView.getText().toString();
                newGroup.setName(groupName.getText().toString());
                newGroup.addUserInBackground(emailText, NewGroupActivity.this, new GetCallback<ParseUser>() {
                    @Override
                    public void done(ParseUser parseUser, ParseException e) {
                        if (e == null) {
                            if (parseUser != null) {
                                members.add(emailText);
                                arrayAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Toast.makeText(NewGroupActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                        pd.dismiss();
                        textView.setText("");
                        View view = NewGroupActivity.this.getCurrentFocus();
                        if (view != null) {
                            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }
                    }
                });

            }
        });
        memberListView = (ListView) findViewById(R.id.listGroupMembers);
        members = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, members);
        memberListView.setAdapter(arrayAdapter);
        buttonGroupProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View arg0){

                Intent i = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_GROUP_ICON);
            }
        });
        Button buttonCreate = (Button) findViewById(R.id.buttonCreateGroup);
        buttonCreate.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick (View v){

//                buttonGroupProfilePicture.buildDrawingCache();
//                Bitmap bitmap = buttonGroupProfilePicture.getDrawingCache();


//                Bitmap bitmap = Bitmap.createBitmap(40, 40, Bitmap.Config.ARGB_8888);
//                Canvas canvas = new Canvas(bitmap);
//                Drawable d = buttonGroupProfilePicture.getDrawable();
//                d.setBounds(0, 0, 40, 40);
//                d.draw(canvas);

//                ByteArrayOutputStream bs = new ByteArrayOutputStream();
//                bitmap.compress(Bitmap.CompressFormat.PNG, 50, bs);
//                intent.putExtra("byteArray", bs.toByteArray());
                final ProgressDialog pd = ProgressDialog.show(NewGroupActivity.this, "Please wait ...", "Saving your new group ...", true);
                pd.setCancelable(false);
                newGroup.setName(groupName.getText().toString());
                newGroup.setDescription(groupDesc.getText().toString());
                newGroup.addUser(ParseUser.getCurrentUser(), true);
                newGroup.setLocalIcon(BitmapFactory.decodeFile(iconPath));
                DB.addGroupInBackground(newGroup, new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            setResult(RESULT_OK);
                            pd.dismiss();
                            finish();

                        } else {
                            pd.setMessage("Error! " + e.getMessage());
                            pd.setCancelable(true);
                        }
                    }
                });
            }
        });
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_GROUP_ICON && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            ImageView imageView = (ImageView) findViewById(R.id.buttonGroupProfilePicture);
            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            iconPath = picturePath;
            //setPic();
            //imageView.setMaxHeight(60);
//            imageView.setMaxWidth(60);


        }
    }

}
