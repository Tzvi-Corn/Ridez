package il.ac.huji.ridez;

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
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;


public class NewGroupActivity extends ActionBarActivity {
    private static int RESULT_GROUP_ICON = 1;
    private EditText groupName, groupDesc;
    private ImageButton buttonGroupProfilePicture;
    private String iconPath = "blank";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);
        groupName = (EditText) findViewById(R.id.edtTextGroupName);
        groupDesc = (EditText) findViewById(R.id.edtTextGroupDescription);
        buttonGroupProfilePicture = (ImageButton) findViewById(R.id.buttonGroupProfilePicture);
        ArrayList<String> emailAddressCollection = new ArrayList<String>();

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

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, emailAddresses);
        AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.searchGruopMembers);
        textView.setAdapter(adapter);


    buttonGroupProfilePicture.setOnClickListener(new View.OnClickListener()

    {

        @Override
        public void onClick (View arg0){

        Intent i = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_GROUP_ICON);
    }
    }

    );
    Button buttonCreate = (Button) findViewById(R.id.buttonCreateGroup);
    buttonCreate.setOnClickListener(new View.OnClickListener()

    {
        @Override
        public void onClick (View v){
        Intent intent = new Intent();

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

                intent.putExtra(MyGroupsActivity.GROUP_NAME, new String[] {groupName.getText().toString(), groupDesc.getText().toString(), iconPath});
//                intent.putExtra(MyGroupsActivity.GROUP_NAME, new String[] {groupName.getText().toString(), groupDesc.getText().toString()});
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_group, menu);
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
