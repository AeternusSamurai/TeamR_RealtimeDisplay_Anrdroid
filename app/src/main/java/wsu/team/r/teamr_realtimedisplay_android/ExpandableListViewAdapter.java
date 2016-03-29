package wsu.team.r.teamr_realtimedisplay_android;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import java.util.HashMap;
import java.util.List;
import wsu.team.r.teamr_realtimedisplay_android.R;

public class ExpandableListViewAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> parentDataSource;
    private HashMap<String, List<String>> childDataSource;

    public ExpandableListViewAdapter(Context context, List<String> childParent, HashMap<String, List<String>> child) {

        this.context = context;
        this.parentDataSource = childParent;
        this.childDataSource = child;

    }

    public void setChildDataSource(HashMap<String, List<String>> dataSource){
        childDataSource = dataSource;
    }

    @Override
    public int getGroupCount() {

        return this.parentDataSource.size();

    }

    @Override
    public int getChildrenCount(int groupPosition) {

        return this.childDataSource.get(this.parentDataSource.get(groupPosition)).size();

    }

    @Override
    public Object getGroup(int groupPosition) {

        return parentDataSource.get(groupPosition);

    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {

        return this.childDataSource.get(parentDataSource.get(groupPosition)).get(childPosition);

    }

    @Override
    public long getGroupId(int groupPosition) {

        return groupPosition;

    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {

        return childPosition;

    }

    @Override
    public boolean hasStableIds() {

        return false;

    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        View view = convertView;

        if(view == null){
            LayoutInflater inflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.parent_layout, parent, false);

            ViewHolder holder = new ViewHolder();
            holder.text = (TextView) view.findViewById(R.id.parent_layout);
            view.setTag(holder);
        }

        ViewHolder holder = (ViewHolder)view.getTag();
        String parentHeader = (String)getGroup(groupPosition);
        TextView parentItem = holder.text;
        parentItem.setText(parentHeader);
        return view;

    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        View view = convertView;

        if(view == null){

            LayoutInflater inflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.child_layout, parent, false);

            ViewHolder holder = new ViewHolder();
            holder.text = (TextView) view.findViewById(R.id.child_layout);
            view.setTag(holder);

        }

        ViewHolder holder = (ViewHolder) view.getTag();
        String childName = (String)getChild(groupPosition, childPosition);
        TextView childItem = holder.text;
        childItem.setText(childName);
        childItem.setId(childPosition);
        return view;

    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {

        return true;

    }

    class ViewHolder{
        public ViewHolder(){

        }

        public TextView text;
    }

}
