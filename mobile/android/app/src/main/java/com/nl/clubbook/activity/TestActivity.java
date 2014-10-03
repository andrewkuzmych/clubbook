package com.nl.clubbook.activity;

import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.nl.clubbook.R;
import com.nl.clubbook.adapter.ContactAdapter;
import com.nl.clubbook.datasource.Contact;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Volodymyr on 11.08.2014.
 */
public class TestActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        AdapterView.OnItemClickListener {

    private final String[] PROJECTION =  {
                    ContactsContract.Contacts._ID,
                    ContactsContract.Contacts.LOOKUP_KEY,
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?  ContactsContract.Contacts.DISPLAY_NAME_PRIMARY : ContactsContract.Contacts.DISPLAY_NAME
            };

    private ContactAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_test);

        initView();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(
                TestActivity.this,
                ContactsContract.Contacts.CONTENT_URI,
                PROJECTION,
                null,
                null,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?  ContactsContract.Contacts.DISPLAY_NAME_PRIMARY : ContactsContract.Contacts.DISPLAY_NAME
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, final Cursor cursor) {
        new AsyncTask<Void, Void, List<Contact>>() {

            @Override
            protected List<Contact> doInBackground(Void... params) {
                List<Contact> contactList = new ArrayList<Contact>();

                if (cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        long id = cursor.getLong(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                        String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                        Cursor emailCur = getContentResolver().query(
                                ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                                new String[]{"" + id}, null);

                        if(emailCur.moveToFirst()) {
                            String email = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));

                            if(email != null && !email.equalsIgnoreCase(name)) {
                                Contact contact = new Contact();
                                contact.setId(id);
                                contact.setName(name);
                                contact.setEmail(email);
//                                contact.setPhoto(retrieveContactPhoto(id)); //TODO

                                contactList.add(contact);
                            }
                        }
                        emailCur.close();
                    }
                }

                return contactList;
            }

            @Override
            protected void onPostExecute(List<Contact> contacts) {
                findViewById(R.id.progressBar).setVisibility(View.GONE);

                mAdapter.updateData(contacts);
            }
        }.execute();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mAdapter.updateData(null);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    private void initView() {
        ListView mContactsList = (ListView) findViewById(R.id.listContacts);
        mAdapter = new ContactAdapter(getBaseContext(), new ArrayList<Contact>());
        mContactsList.setAdapter(mAdapter);
        mContactsList.setOnItemClickListener(this);

        getSupportLoaderManager().initLoader(0, null, this);
    }

//    private Bitmap retrieveContactPhoto(long contactId) {
//        Bitmap photo = null;
//
//        try {
//            InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(getContentResolver(),
//                    ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId));
//
//            if (inputStream != null) {
//                photo = BitmapFactory.decodeStream(inputStream);
//                inputStream.close();
//            }
//        } catch (IOException e) {
//            L.i("" + e);
//        }
//
//        return photo;
//    }
}
