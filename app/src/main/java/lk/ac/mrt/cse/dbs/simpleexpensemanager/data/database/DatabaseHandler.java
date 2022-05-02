package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final String dbname = "expenseManger.db";

    public DatabaseHandler(@Nullable Context context) {
        super(context, dbname, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query1 = "CREATE TABLE " + Entry.TABLE_NAME_ACCOUNT + " (" +
               Entry.ACCOUNT_ID + " TEXT PRIMARY KEY," +
                Entry.ACCOUNT_BANK + " TEXT," +
               Entry.ACCOUNT_HOLDER + " TEXT," +
               Entry.ACCOUNT_BALANCE + " VARCHAR)";

        String query2 = "CREATE TABLE " + Entry.TABLE_NAME_TRANSACTION + " (" +
                Entry.TRANSACTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                Entry.TRANSACTION_DATE + " TEXT," +
                Entry.ACCOUNT_ID + " TEXT," +
                Entry.TRANSACTION_TYPE + " TEXT," +
                Entry.TRANSACTION_AMOUNT + " VARCHAR)";
        sqLiteDatabase.execSQL(query1);
        sqLiteDatabase.execSQL(query2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String querytran = "DROP TABLE IF EXISTS " + Entry.TABLE_NAME_TRANSACTION;
        String queryacc = "DROP TABLE IF EXISTS " + Entry.TABLE_NAME_ACCOUNT;
        sqLiteDatabase.execSQL(queryacc);
        sqLiteDatabase.execSQL(querytran);
        onCreate(sqLiteDatabase);
    }

    public List<String> getIndexNumbersList(String query){

        List<String> indexNumbers=new ArrayList<String>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query,null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                String id=cursor.getString(0);
                // Adding id to list
                indexNumbers.add(id);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return indexNumbers;
    }

    public List<Transaction> getAllTransactionList(String query) {

        List<Transaction> transactionList=new ArrayList<Transaction>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query,null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                String date=cursor.getString(1);
                SimpleDateFormat formatter=new SimpleDateFormat("EEE MMM dd HH:mm:ss zzzz yyyy");
                Date datef= null;
                try {
                    datef = formatter.parse(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String accoutNo=cursor.getString(2);
                String type=cursor.getString(3);
                Integer amount=cursor.getInt(4);
                // Adding id to list
                ExpenseType atype;
                if (type=="EXPENSE"){
                  atype=ExpenseType.EXPENSE;
                }
                else {
                    atype=ExpenseType.INCOME;
                }
                Transaction transaction =new Transaction(datef,accoutNo,atype,amount);
                transactionList.add(transaction);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return transactionList;
    }

    public List<Account> getAllAccountList(String query) {

        List<Account> accountList=new ArrayList<Account>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query,null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                String accountNo=cursor.getString(0);
                String bank=cursor.getString(1);
                String holder=cursor.getString(2);
                double amount=cursor.getInt(3);
                Account account =new Account( accountNo,bank,holder,amount);
                accountList.add(account);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return accountList;
    }


    // Getting contacts Count
    public int getRowCount(String query) {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        int rowcount=cursor.getCount();
        cursor.close();
        // return count
        return rowcount;
    }
    // code to get the single account
    public Account getAcount(String id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Entry.TABLE_NAME_ACCOUNT, new String[] { Entry.ACCOUNT_ID,
                        Entry.ACCOUNT_BANK,
                        Entry.ACCOUNT_HOLDER,
                        Entry.ACCOUNT_BALANCE }, Entry.ACCOUNT_ID + "=?",
                new String[] { id }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        Account account =new Account(cursor.getString(0),cursor.getString(1),
                cursor.getString(3),Double.parseDouble(cursor.getString(3)));

        // return contact
        return account;
    }

    // Deleting single account
    public void deleteContact(String accNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Entry.TABLE_NAME_ACCOUNT, Entry.ACCOUNT_ID + " = ?",
                new String[] { accNo });
        db.close();
    }
    // code to get the single account balance
    double getAcountBalance(String id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Entry.TABLE_NAME_ACCOUNT, new String[] { Entry.ACCOUNT_ID,
                        Entry.ACCOUNT_BALANCE }, Entry.ACCOUNT_ID + "=?",
                new String[] { id }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        // return contact
        return Double.parseDouble(cursor.getString(1));
    }
    // code to update the single contact
    public int updateamount(String accountNo, ExpenseType expenseType, double amount) {
        SQLiteDatabase db = this.getWritableDatabase();

        double balance=getAcountBalance(accountNo);
        // specific implementation based on the transaction type
        switch (expenseType) {
            case EXPENSE:
                balance=balance-amount;
                break;
            case INCOME:
                balance=balance+amount;
                break;
        }

        ContentValues values = new ContentValues();
        values.put( Entry.ACCOUNT_BALANCE, balance);

        // updating row
        return db.update(Entry.TABLE_NAME_ACCOUNT, values, Entry.ACCOUNT_ID + " = ?",
                new String[] { accountNo });
    }
    public String insertAccount(Account account) {
        // Gets the data repository in write mode
        SQLiteDatabase db = this.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(Entry.ACCOUNT_ID, account.getAccountNo());
        values.put(Entry.ACCOUNT_BANK, account.getBankName());
        values.put(Entry.ACCOUNT_HOLDER, account.getAccountHolderName());
        values.put(Entry.ACCOUNT_BALANCE, account.getBalance());

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(Entry.TABLE_NAME_ACCOUNT, null, values);

        if (newRowId == -1) return "Failed";
        else return "Successfully Inserted!!!";
    }

    public String insertTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        // Gets the data repository in write mode
        SQLiteDatabase db = this.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(Entry.TRANSACTION_DATE, String.valueOf(date));
        values.put(Entry.ACCOUNT_ID, accountNo);
        values.put(Entry.TRANSACTION_TYPE, String.valueOf(expenseType));
        values.put(Entry.TRANSACTION_AMOUNT, amount);

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(Entry.TABLE_NAME_TRANSACTION, null, values);
        db.close();
        if (newRowId == -1) return "Failed";
        else return "Successfully Inserted!!!";
    }

    /* Inner class that defines the table contents */
    public static class Entry implements BaseColumns {
        public static final String TABLE_NAME_ACCOUNT = "account";
        public static final String ACCOUNT_ID = "account_id";
        public static final String ACCOUNT_BANK = "account_bank";
        public static final String ACCOUNT_HOLDER = "account_holder";
        public static final String ACCOUNT_BALANCE = "account_balance";

        public static final String TABLE_NAME_TRANSACTION = "transactions";
        public static final String TRANSACTION_ID = "transaction_id";
        public static final String TRANSACTION_DATE = "transaction_date";
        public static final String TRANSACTION_TYPE = "transaction_type";
        public static final String TRANSACTION_AMOUNT = "transaction_amount";


    }
}
