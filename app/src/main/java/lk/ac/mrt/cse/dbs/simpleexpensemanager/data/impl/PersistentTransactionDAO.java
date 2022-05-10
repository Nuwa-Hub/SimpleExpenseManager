package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.database.DatabaseHandler;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class PersistentTransactionDAO extends DatabaseHandler implements TransactionDAO  {


    public PersistentTransactionDAO(@Nullable Context context) {
        super(context);
    }

    @Override
    public String logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        return insertTransaction(date,accountNo,expenseType,amount);
    }

    @Override
    public List<Transaction> getAllTransactionLogs()  {
        String query=" SELECT * FROM transactions ";
        return getAllTransactionList(query);

    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit)  {
        List<Transaction> transactionList=new ArrayList<Transaction>();
        String query=" SELECT * FROM transactions";
        int transactionsnum=getRowCount(query);
        transactionList=getAllTransactionLogs();
        if (transactionsnum <= limit) {
            return transactionList;
        }
        // return the last <code>limit</code> number of transaction logs

        return transactionList.subList(transactionsnum - limit, transactionsnum);
    }




}
