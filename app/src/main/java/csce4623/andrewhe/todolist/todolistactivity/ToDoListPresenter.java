package csce4623.andrewhe.todolist.todolistactivity;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.List;

import csce4623.andrewhe.todolist.data.ToDoItem;
import csce4623.andrewhe.todolist.data.ToDoItemRepository;
import csce4623.andrewhe.todolist.data.ToDoListDataSource;

/**
 * ToDoListPresenter -- Implements the Presenter interface from ToDoListContract Presenter
 */
public class ToDoListPresenter implements ToDoListContract.Presenter {

    //Data repository instance
    //Currently has a memory leak -- need to refactor context passing
    private static ToDoItemRepository mToDoItemRepository;
    //View instance
    private final ToDoListContract.View mToDoItemView;

    // Integer request codes for creating or updating through the result method
    private static final int CREATE_TODO_REQUEST = 0;
    private static final int UPDATE_TODO_REQUEST = 1;
    private static final int DELETE_TODO_REQUEST = 2;

    /**
     * ToDoListPresenter constructor
     * @param toDoItemRepository - Data repository instance
     * @param toDoItemView - ToDoListContract.View instance
     */
    public ToDoListPresenter(@NonNull ToDoItemRepository toDoItemRepository, @NonNull ToDoListContract.View toDoItemView){
        mToDoItemRepository = toDoItemRepository;
        mToDoItemView = toDoItemView;
        //Make sure to pass the presenter into the view!
        mToDoItemView.setPresenter(this);
    }

    @Override
    public void start(){
        //Load all toDoItems
        loadToDoItems();
    }


    @Override
    public void addNewToDoItem(ToDoItem item) {
        Log.d("ToDoListPresenter", "Add New ToDoItem");
        //Show AddEditToDoItemActivity with a create request and temporary item
        mToDoItemView.showAddEditToDoItem(item, CREATE_TODO_REQUEST);
    }

    public void showExistingToDoItem(ToDoItem item) {
        //Show AddEditToDoItemActivity with a edit request, passing through an item
        Log.d("ToDoListPresenter", "TODO: Show Existing ToDoItem");
        mToDoItemView.showAddEditToDoItem(item, UPDATE_TODO_REQUEST);
    }

    @Override
    public void result(int requestCode, int resultCode, ToDoItem item) {
        Log.d("ToDoListPresenter", "resultCode: " + resultCode + " requestCode: " + requestCode);

        if(resultCode == Activity.RESULT_OK){
            if(requestCode == CREATE_TODO_REQUEST){
                createToDoItem(item);
            } else if (requestCode == UPDATE_TODO_REQUEST){
                    updateToDoItem(item);
            } else {
                Log.e("ToDoPresenter", "No such request!");
            }
        } else if (resultCode == DELETE_TODO_REQUEST) {
            Log.d("ToDoListPresenter", "Delete Item");
            deleteToDoItem(item);
        }
    }

    /**
     * Create ToDoItem in repository from ToDoItem and reload data
     * @param item - item to be placed in the data repository
     */
    private void createToDoItem(ToDoItem item){
        Log.d("ToDoListPresenter","Create Item");
        mToDoItemRepository.createToDoItem(item);
    }

    private void deleteToDoItem(ToDoItem item) {
        Log.d("ToDoListPresenter", "Delete Item");
        mToDoItemRepository.deleteToDoItem(item);
    }

    /**
     * Update ToDoItem in repository from ToDoItem and reload data
     * @param item -- ToDoItem to be updated in the ToDoItemRepository
     */
    @Override
    public void updateToDoItem(ToDoItem item){
        Log.d("ToDoListPresenter", "Update Item");
        Log.d("ToDoListPresenter", item.getContent());
        mToDoItemRepository.saveToDoItem(item);
    }

    /**
     * loadToDoItems -- loads all items from ToDoItemRepository
     * Two callbacks -- success/failure
     */
    @Override
    public void loadToDoItems(){
        Log.d("ToDoListPresenter","Loading ToDoItems");
        mToDoItemRepository.getToDoItems(new ToDoListDataSource.LoadToDoItemsCallback() {
            @Override
            public void onToDoItemsLoaded(List<ToDoItem> toDoItems) {
                Log.d("PRESENTER","Loaded");
                mToDoItemView.showToDoItems(toDoItems);
            }

            @Override
            public void onDataNotAvailable() {
                Log.d("PRESENTER","Not Loaded");
            }
        });
    }
}
