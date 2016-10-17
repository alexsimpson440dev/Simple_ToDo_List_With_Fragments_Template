package com.clara.simple_todo_list_with_fragments;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements
		AddToDoItemFragment.NewItemCreatedListener,
		ToDoItemDetailFragment.MarkItemAsDoneListener,
		ToDoListFragment.ListItemSelectedListener {


	private static final String TODO_ITEMS_KEY = "TODO ITEMS ARRAY LIST";
	private static final String ADD_NEW_FRAG_TAG = "ADD NEW FRAGMENT";
	private static final String LIST_FRAG_TAG = "LIST FRAGMENT";
	private static final String DETAIL_FRAG_TAG = "DETAIL FRAGMENT";

	private ArrayList<ToDoItem> mTodoItems;

	private static final String TAG = "MAIN ACTIVITY";


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			//if no saved instance state, then app has just started
			//create ArrayList, create Add and List fragments and add to this Activity
			Log.d(TAG, "onCreate has no instance state. Setup arraylist");

			mTodoItems = new ArrayList<>();

			AddToDoItemFragment addNewFragment = AddToDoItemFragment.newInstance();
			ToDoListFragment listFragment = ToDoListFragment.newInstance(mTodoItems);

			FragmentManager fm = getSupportFragmentManager();
			FragmentTransaction ft = fm.beginTransaction();

			ft.add(R.id.add_todo_view_container, addNewFragment, ADD_NEW_FRAG_TAG);
			ft.add(R.id.todo_list_view_container, listFragment, LIST_FRAG_TAG);

			ft.commit();

			} else {

			//else, if there is saved instance state, then app is currently running
			//get ArrayList from saved instance state
			mTodoItems = savedInstanceState.getParcelableArrayList(TODO_ITEMS_KEY);
			Log.d(TAG, "onCreate has saved instance state ArrayList = " + mTodoItems);
		}

	}

	@Override
	public void onSaveInstanceState(Bundle outBundle) {
		//save ArrayList in outBundle
		super.onSaveInstanceState(outBundle);
		outBundle.putParcelableArrayList(TODO_ITEMS_KEY, mTodoItems);
	}

	@Override
	public void newItemCreated(ToDoItem newItem) {
		//Add new item to list, notify ToDoListFragment that it needs to update
		mTodoItems.add(newItem);

		Log.d(TAG, "newItemCreated = " + mTodoItems);

		//get reference to list Fragment from the FragmentManager,
		//and tell this Fragment that the data set has changed
		FragmentManager fm = getSupportFragmentManager();
		ToDoListFragment listFragment = (ToDoListFragment) fm.findFragmentByTag(LIST_FRAG_TAG);
		listFragment.notifyItemsChanged();
	}

	@Override
	public void itemSelected(ToDoItem selected) {
		//Show ToDoDetailFragment for this Todo item
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

		//create a new detail fragment. add it to the activity
		ToDoItemDetailFragment detailFragment = ToDoItemDetailFragment.newInstance(selected);
		ft.add(android.R.id.content, detailFragment);
		//add to the back stack so if the user presses back it will revert to this transaction
		ft.addToBackStack(DETAIL_FRAG_TAG);

		ft.commit();
	}

	@Override
	public void todoItemDone(ToDoItem doneItem) {
		//remove from ArrayList.
		mTodoItems.remove(doneItem);

		Log.d(TAG, "newItemRemoved list is now = " + mTodoItems);

		//find list fragment and tell it that the data has changed
		FragmentManager fm = getSupportFragmentManager();
		ToDoListFragment listFragment = (ToDoListFragment) fm.findFragmentByTag(LIST_FRAG_TAG);
		listFragment.notifyItemsChanged();

		//revert the last fragment transaction on the back stack.
		//this removes the detail fragment from the activity, which leaves the Add+List fragments.

		FragmentTransaction ft = fm.beginTransaction();
		fm.popBackStack();
		ft.commit();
	}
}


