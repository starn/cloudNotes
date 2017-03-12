package fr.starn.cloudNotes;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.*;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by starn on 11/03/2017.
 */
public class ContactMgt {

    private final static String name = "zz_CloudNotes";
    private final static String phone = "00000000000";


    public static String getContactAddress(Context ctx){

        String contactID  = getContactIDFromName(ctx, name);

        if (contactID==null){
            Intent createContactIntent = new Intent(ctx, ConfigActivity.class);
            ctx.startActivity(createContactIntent);
            return "Virtual contact not created yet. Please select your account before (google contact?).";
//            contactID  = getContactIDFromName(ctx, name);
//            if (contactID==null) return "Error during virtual contact creation";
        }

        // get the data package containg the postal information for the contact
        Cursor cursor = ctx.getContentResolver().query(ContactsContract.Data.CONTENT_URI,
                new String[]{ ContactsContract.CommonDataKinds.StructuredPostal.STREET,
                        ContactsContract.CommonDataKinds.StructuredPostal.CITY,
// add more coluns from StructuredPostal if you need them
                        ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE},
                ContactsContract.Data.CONTACT_ID + "=? AND " +
                        ContactsContract.CommonDataKinds.StructuredPostal.MIMETYPE + "=?",
                new String[]{String.valueOf(contactID), ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE},
                null);

        cursor.moveToFirst();
        String street = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET));
        String postcode = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE));
        String city = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY));

        return street;
    }


    public static void createContact(Context ctx, String accountType, String accountName){
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        int rawContactInsertIndex = ops.size();

        ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, accountType)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME,accountName).build());

        //Phone Number
        ops.add(ContentProviderOperation
                .newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,
                        rawContactInsertIndex)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phone)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, "1")
                .build());

        //Display name/Contact name
        ops.add(ContentProviderOperation
                .newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,
                        rawContactInsertIndex)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name )
                .build());



        //Postal Address

        ops.add(ContentProviderOperation
                .newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,
                        rawContactInsertIndex)
//                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE )
//                .withValue(ContactsContract.CommonDataKinds.StructuredPostal.POBOX, "Postbox")

                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE )
                .withValue(ContactsContract.CommonDataKinds.StructuredPostal.STREET, "street")

//                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE )
//                .withValue(ContactsContract.CommonDataKinds.StructuredPostal.CITY, "city")
//
//                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE )
//                .withValue(ContactsContract.CommonDataKinds.StructuredPostal.REGION, "region")
//
//                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE )
//                .withValue(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE, "postcode")
//
//                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE )
//                .withValue(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY, "country")

                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE )
                .withValue(ContactsContract.CommonDataKinds.StructuredPostal.TYPE, "3")


                .build());



        try {
            ContentProviderResult[] res = ctx.getContentResolver().applyBatch(
                    ContactsContract.AUTHORITY, ops);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


    public static void updateAddress(Context ctx, String address){
//        String selectPhone = ContactsContract.Data.CONTACT_ID + "=?";
//        String[] phoneArgs = new String[]
//                {String.valueOf(getContactIDFromName(ctx, name))};
//
//        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
//        int rawContactInsertIndex = ops.size();
//        ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
//                .withSelection(selectPhone, phoneArgs)
//                .withValue(ContactsContract.CommonDataKinds.StructuredPostal.STREET, address)
////                .withValue(ContactsContract.CommonDataKinds.StructuredPostal.CITY, CITY)
////                .withValue(ContactsContract.CommonDataKinds.StructuredPostal.REGION, REGION)
////                .withValue(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE, POSTCODE)
////                .withValue(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY, COUNTRY)
//                //.withValue(ContactsContract.Data.MIMETYPE, "vnd.android.cursor.item/postal-address_v2")
//                .build());
//        try {
//            ctx.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
//        } catch (Exception e){
//            e.printStackTrace();
//        }

        String baseSelection = ContactsContract.Data.CONTACT_ID + "=?";
        String[] baseSelectionArgs = new String[] { String.valueOf(getContactIDFromName(ctx, name)) };

        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
//        String selection = baseSelection + " AND " + ContactsContract.Data.MIMETYPE + "=?";
//        String[] selectionArgs = new String[] { baseSelectionArgs[0],
//                ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE };
//        ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
//                .withSelection(selection, selectionArgs)
//                .withValue(ContactsContract.CommonDataKinds.StructuredPostal.STREET, address)
//                //.withValue(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, surname)
//                .build());


        String selection = baseSelection + " AND " +                       ContactsContract.Data.MIMETYPE + "=?                    ";
        String[] selectionArgs = new String[] { baseSelectionArgs[0],      ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE };
        ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                .withSelection(selection, selectionArgs)
                .withValue(ContactsContract.CommonDataKinds.StructuredPostal.STREET, address)
                .build());

        try {
            ctx.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (Exception e){
            e.printStackTrace();
        }

    }

        public static boolean deleteContactByPhoneAndName(Context ctx, String phone, String name) {
            Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phone));
            Cursor cur = ctx.getContentResolver().query(contactUri, null, null, null, null);
            try {
                if (cur.moveToFirst()) {
                    do {
                        if (cur.getString(cur.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME)).equalsIgnoreCase(name)) {
                            String lookupKey = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
                            Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey);
                            ctx.getContentResolver().delete(uri, null, null);
                            return true;
                        }

                    } while (cur.moveToNext());
                }

            } catch (Exception e) {
                System.out.println(e.getStackTrace());
            } finally {
                cur.close();
            }
            return false;
    }

    public static void deleteContactByName(Context ctx, String name){
        ContentResolver cr = ctx.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        while (cur.moveToNext()) {
            try{
                String lookupKey = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
                Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey);
                String contactName= getDisplayName(ctx,uri);
                System.out.println("The uri is " + uri.toString()+ "=>"+contactName);
                if (name.equals(contactName)) {
                    System.out.println("delete "+contactName);
                    cr.delete(uri, null, null);
                }
            }
            catch(Exception e)
            {
                System.out.println(e.getStackTrace());
            }
        }
    }




    private static String getDisplayName(Context ctx, Uri contactUri) {
        String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER};
        Cursor queryCursor = ctx.getContentResolver()
                .query(contactUri, null, null, null, null);
        queryCursor.moveToFirst();
        String name = queryCursor.getString(queryCursor.getColumnIndex("display_name"));
        String id = queryCursor.getString(
                queryCursor.getColumnIndex(ContactsContract.Contacts._ID));

        if (Integer.parseInt(queryCursor.getString(queryCursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
            Cursor pCur = ctx.getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                    new String[]{id}, null);
            while (pCur.moveToNext()) {
                String number = pCur.getString(pCur.getColumnIndex("data1"));
                System.out.println("Contact Name: "+ number);
            }
            pCur.close();
        }


        return name;
    }

    public static String getContactIDFromName(Context ctx, String contactName) {
        Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_FILTER_URI, Uri.encode(contactName.trim()));
        Cursor mapContact = ctx.getContentResolver().query(uri, new String[]{ContactsContract.PhoneLookup._ID}, null, null, null);
            if (mapContact.moveToNext()) {
                String id = mapContact.getString(mapContact.getColumnIndex(ContactsContract.Contacts._ID));
                return id;
            }
            return null;
    }

    public static String[] getAccounts(Context context){
            AccountManager manager = AccountManager.get(context);
            Account[] accounts = manager.getAccounts();
            List<String> possibleEmails = new LinkedList<>();

            for (Account account : accounts) {

                // account.name as an email address only for certain account.type values.
                possibleEmails.add(account.name);
                System.out.println("account:"+account.name+","+account.type);
            }

//            if (!possibleEmails.isEmpty() && possibleEmails.get(0) != null) {
//                String email = possibleEmails.get(0);
//                return email;
//
//            }
//            return null;
        return null;
        }
}
