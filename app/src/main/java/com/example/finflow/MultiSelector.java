package com.example.finflow;

import android.util.SparseBooleanArray;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class MultiSelector {
    private SparseBooleanArray selectedItems = new SparseBooleanArray();

    public boolean tapSelection(MainActivity1.SimpleAdapter.VerticalItemHolder view) {
        if (view != null && view.itemView != null) {
            int position = (int) view.itemView.getTag();
            boolean isSelected = selectedItems.get(position, false);
            setSelected(view, !isSelected);
            return true;
        } else {
            return false;
        }
    }

    public void setSelected(View view, boolean selected) {
        int position = (int) view.getTag();
        if (selected) {
            selectedItems.put(position, true);
            view.setBackgroundResource(android.R.color.holo_blue_light);
        } else {
            selectedItems.delete(position);
            view.setBackgroundResource(android.R.color.transparent);
        }
    }

    public boolean isSelected(int position, int i) {
        return selectedItems.get(position, false);
    }

    public void clearSelections() {
        selectedItems.clear();
    }

    public SparseBooleanArray getSelectedPositions() {
        return selectedItems;
    }

    public void setSelected(MainActivity1.SimpleAdapter.VerticalItemHolder verticalItemHolder, boolean selected) {
        setSelected(verticalItemHolder.itemView, selected);
    }

    public List<Integer> getSelectedIndexes() {
        List<Integer> indexes = new ArrayList<>();
        for (int i = 0; i < selectedItems.size(); i++) {
            if (selectedItems.valueAt(i)) {
                indexes.add(selectedItems.keyAt(i));
            }
        }
        return indexes;
    }
}
