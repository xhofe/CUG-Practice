package com.ambow.contact;

import com.ambow.contact.R;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class Contacts extends ListActivity {
	private static final String TAG = "Contacts";
	
	private static final int AddContact_ID = Menu.FIRST;
	private static final int DeleteContact_ID = Menu.FIRST + 1;
	private static final int EditContact_ID = Menu.FIRST + 2;
	private static final int SearchContact_ID = Menu.FIRST + 3;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Intent intent = getIntent();
        if (intent.getData() == null) {
            intent.setData(ContactsProvider.CONTENT_URI);
        }
        
        getListView().setOnCreateContextMenuListener(this);
        setAdapter();

    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        
        menu.add(0, AddContact_ID, 0, R.string.menu_add)
           	.setIcon(android.R.drawable.ic_menu_add);
        menu.add(0, SearchContact_ID, 0, R.string.menu_search)
       	.setIcon(android.R.drawable.ic_menu_search);
    
        return true;    
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case AddContact_ID:
            startActivity(new Intent(Intent.ACTION_INSERT, getIntent().getData()));
            return true;
        case SearchContact_ID:
            LayoutInflater factory = LayoutInflater.from(this);
            final View textEntryView = factory.inflate(R.layout.alert_enter, null);
            new AlertDialog.Builder(Contacts.this)
                .setIcon(R.drawable.icon)
                .setTitle(R.string.menu_search)
                .setView(textEntryView)
                .setPositiveButton("查找", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    	EditText sEdit =(EditText)textEntryView.findViewById(R.id.search_edit);
                    	String keyword = sEdit.getText().toString();
                    	String selection = "";
                    	if(keyword!=""){
                        	selection = ContactColumn.NAME+" like '%"+ keyword +"%' or "
                        	          + ContactColumn.MOBILE+" like '%"+ keyword +"%' or "
                        	          + ContactColumn.COMPANY+"  like '%"+ keyword +"%' or "
                                      +ContactColumn.QQ+"  like '%"+ keyword +"%'";
                    	}
                    	Log.d(TAG,"selection=" + selection);
                    	
                    	String message ="";
                    	Cursor tCursor = managedQuery(getIntent().getData(), ContactColumn.PROJECTION, selection, null, null);
                        if(tCursor.getCount()>0){
                        	tCursor.moveToFirst();
                        	while(!tCursor.isAfterLast()){
                                message += "公司："+ tCursor.getString(4)+"\n"
                                        + "姓名："+ tCursor.getString(ContactColumn.NAME_COLUMN) +"\n"
                                        + "电话："+ tCursor.getString(ContactColumn.MOBILE_COLUMN) +"\n"
                                        + "邮箱："+ tCursor.getString(ContactColumn.EMAIL_COLUMN) +"\n"
                                        + " Q Q :"+tCursor.getString(ContactColumn.QQ_COLUMN)+"\n"
                                        + "-----------------------\n";
                                tCursor.moveToNext();
                        	} 
                        }else{
                        	message ="找不到该记录！";	
                        } 
                        
                        new AlertDialog.Builder(Contacts.this)
                        .setIcon(R.drawable.icon)
                        .setTitle("查找结果")
                        .setMessage(message)
                        .setPositiveButton("关闭", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                            }
                        }).show();

                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                }).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Uri uri = ContentUris.withAppendedId(getIntent().getData(), id);
        String action = getIntent().getAction();
        if (Intent.ACTION_PICK.equals(action) || Intent.ACTION_GET_CONTENT.equals(action)) {
            setResult(RESULT_OK, new Intent().setData(uri));
        } else {
            startActivity(new Intent(Intent.ACTION_EDIT, uri));
        }
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info;
        try {
             info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        } catch (ClassCastException e) {
            return;
        }

        Cursor cursor = (Cursor) getListAdapter().getItem(info.position);
        if (cursor == null) {
            return;
        }
        menu.setHeaderTitle(cursor.getString(1));
        menu.add(0, DeleteContact_ID, 0, R.string.menu_delete);
        menu.add(0, EditContact_ID, 0, R.string.menu_edit);
    }
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info;
        try {
             info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        } catch (ClassCastException e) {
            return false;
        }

        Uri uri = ContentUris.withAppendedId(getIntent().getData(), info.id);
        switch (item.getItemId()) {
            case DeleteContact_ID: {             
                getContentResolver().delete(uri, null, null);
                setAdapter();
                return true;
            }case EditContact_ID: {
            	startActivity(new Intent(Intent.ACTION_EDIT, uri));
            	return true;
            }
        }
        return false;
    }
    
    private void setAdapter(){  	
        Cursor cursor = managedQuery(getIntent().getData(), ContactColumn.PROJECTION, null, null,null);
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.contact_list_item, cursor,
                new String[] {ContactColumn.NAME, ContactColumn.MOBILE, ContactColumn.COMPANY,ContactColumn.QQ  },
                new int[] {android.R.id.text1,android.R.id.text2, R.id.text3,R.id.text4});
        setListAdapter(adapter);
    }
}