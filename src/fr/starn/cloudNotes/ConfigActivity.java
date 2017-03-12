package fr.starn.cloudNotes;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by starn on 12/03/2017.
 */
public class ConfigActivity extends Activity {
    private Dialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.config_layout);
        showDialog();
    }

    public void showDialog(){
        ArrayList<String> gUsernameList = new ArrayList<String>();
        AccountManager accountManager = AccountManager.get(this);
        //Account[] accounts = accountManager.getAccountsByType("com.google");
        Account[] accounts = accountManager.getAccounts();

        gUsernameList.clear();
//loop
        for (Account account : accounts) {
            gUsernameList.add(account.name);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose the account for storing your notes");

        ListView lv = new ListView(this);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this,android.R.layout.simple_list_item_1, android.R.id.text1,
                        gUsernameList);

        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long
                    id)
            {
                String selectedAccountName = gUsernameList.get(position);
                System.out.println("You-select-gmail-account: "+ selectedAccountName);
                String selectedAccountType = "";
                for (Account a: accounts){
                    if (a.name.equals(gUsernameList.get(position))) selectedAccountType=a.type;
                }
                ContactMgt.createContact(ConfigActivity.this,selectedAccountType,selectedAccountName );
                dialog.dismiss();
                ConfigActivity.this.finish();
            }
        });

        builder.setView(lv);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });

        dialog = builder.create();
        dialog.show();
    }
}