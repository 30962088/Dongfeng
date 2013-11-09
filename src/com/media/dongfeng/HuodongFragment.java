package com.media.dongfeng;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RejectedExecutionException;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.media.dongfeng.exception.ZhiDaoApiException;
import com.media.dongfeng.exception.ZhiDaoIOException;
import com.media.dongfeng.exception.ZhiDaoParseException;
import com.media.dongfeng.model.Content;
import com.media.dongfeng.model.ContentList;
import com.media.dongfeng.model.User;
import com.media.dongfeng.net.NetDataSource;
import com.media.dongfeng.utils.Constants;
import com.media.dongfeng.utils.Utils;
import com.media.dongfeng.view.ItemView;
import com.media.dongfeng.view.RTPullListView;

public class HuodongFragment extends Fragment {

    private GetDataTask mGetDataTask;
    
    private EditText etSearchText;
    private ImageView mClearBtn;
    private TextView mEmptyView;
    
    private String mSearchText;
    
    private List<Content> mHuodongList = new ArrayList<Content>();
    
    private HuodongAdapter mAdapter;
    private HuodongAdapter mSearchAdapter;
    
    private boolean mHasInit = false;
    private boolean mIsShowSearch = false;
    
    public HuodongFragment() {
        mAdapter = new HuodongAdapter();
        mSearchAdapter = new HuodongAdapter();
        mGetDataTask = new GetDataTask(mAdapter, mSearchAdapter);
    }
    
    @Override
    public void onCreate( Bundle savedInstanceState ) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated( Bundle savedInstanceState ) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        mEmptyView = (TextView) getView().findViewById(R.id.empty);
        
        RTPullListView mListView;
        RTPullListView mSearchListView;
        
        mListView = (RTPullListView) getView().findViewById(R.id.lvHuodong);
        mListView.setonRefreshListener(new RTPullListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // TODO Auto-generated method stub
                try {
                    if (TextUtils.isEmpty(mSearchText)) {
                        mGetDataTask.switchListViewMode(false);
                        mGetDataTask.RefreshList(MainTabActivity.mUser, true);
                    }
                } catch (Exception e) {}
            }
        });
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged( AbsListView view, int scrollState ) {
            }
            @Override
            public void onScroll( AbsListView view, int firstVisibleItem, int visibleItemCount,
                    int totalItemCount ) {
                // TODO Auto-generated method stub
                if (TextUtils.isEmpty(mSearchText)) {
                    if (totalItemCount > 0 && totalItemCount == firstVisibleItem + visibleItemCount) {
//                        Log.d("net", "mListView onScroll loadMore firstVisibleItem="+firstVisibleItem+"  visibleItemCount="+visibleItemCount+"    totalItemCount="+totalItemCount);
                        mGetDataTask.switchListViewMode(false);
                        mGetDataTask.LoadMoreList(MainTabActivity.mUser);
                    }
                }
            }
        });
        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick( AdapterView<?> parent, View view, int position, long id ) {
                // TODO Auto-generated method stub
                if (position <= 0) {
                    return;
                }
                Content content = mGetDataTask.mList.get(position-1);
                addHasReadContent(content);
                FragmentTransaction transation = getFragmentManager().beginTransaction();
                transation.setCustomAnimations(R.anim.enter_right, 0, 0, 0);
                SucaiHuodongDetailFragment fragment = new SucaiHuodongDetailFragment(content, false);
                transation.replace(R.id.huodong_container, fragment, HuoDongActivity.HUODONG_DETAIL_FRAGMENT);
                transation.addToBackStack(HuoDongActivity.HUODONG_DETAIL_FRAGMENT);
                transation.commit();
            }
        });
        mListView.setAdapter(mAdapter);

        mSearchListView = (RTPullListView) getView().findViewById(R.id.lvSearchHuodong);
        mSearchListView.setonRefreshListener(new RTPullListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // TODO Auto-generated method stub
                try {
                    if (!TextUtils.isEmpty(mSearchText)) {
                        mGetDataTask.switchListViewMode(true);
                        mGetDataTask.RefreshSearchList(MainTabActivity.mUser, mSearchText, true);
                    }
                } catch (Exception e) {}
            }
        });
        mSearchListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged( AbsListView view, int scrollState ) {
            }
            @Override
            public void onScroll( AbsListView view, int firstVisibleItem, int visibleItemCount,
                    int totalItemCount ) {
                // TODO Auto-generated method stub
                if (!TextUtils.isEmpty(mSearchText)) {
                if (totalItemCount > 0 && totalItemCount == firstVisibleItem + visibleItemCount) {
//                    Log.d("net", "mSearchListView onScroll loadMore firstVisibleItem="+firstVisibleItem+"  visibleItemCount="+visibleItemCount+"    totalItemCount="+totalItemCount);
                    mGetDataTask.switchListViewMode(true);
                    mGetDataTask.LoadMoreSearchList(MainTabActivity.mUser, mSearchText);
                }
                }
            }
            
        });
        mSearchListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick( AdapterView<?> parent, View view, int position, long id ) {
                // TODO Auto-generated method stub
                if (position <= 0) {
                    return;
                }
                Content content = mGetDataTask.mSearchList.get(position-1);
                addHasReadContent(content);
                FragmentTransaction transation = getFragmentManager().beginTransaction();
                transation.setCustomAnimations(R.anim.enter_right, 0, 0, 0);
                SucaiHuodongDetailFragment fragment = new SucaiHuodongDetailFragment(content, false);
                transation.replace(R.id.huodong_container, fragment, HuoDongActivity.HUODONG_DETAIL_FRAGMENT);
                transation.addToBackStack(HuoDongActivity.HUODONG_DETAIL_FRAGMENT);
                transation.commit();
            }
        });
        mSearchListView.setAdapter(mSearchAdapter);
        
        mGetDataTask.setContext(getActivity());
        mGetDataTask.setListView(mListView);
        mGetDataTask.setSearchListView(mSearchListView);
        mGetDataTask.setEmptyView(mEmptyView);
        if (mIsShowSearch) {
            mSearchListView.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.GONE);
        } else {
            mSearchListView.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
        }
        
        mClearBtn = (ImageView) getView().findViewById(R.id.clearBtn);
        mClearBtn.setVisibility(View.GONE);
        mClearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                mSearchText = null;
                etSearchText.setText("");
                mGetDataTask.switchListViewMode(false);
            }
        });
        etSearchText = (EditText) getView().findViewById(R.id.etSearchText);
        etSearchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                    int count) {
                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    mClearBtn.setVisibility(View.GONE);
                } else {
                    mClearBtn.setVisibility(View.VISIBLE);
                }
            }
        });
        etSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction( TextView v, int actionId, KeyEvent event ) {
                // TODO Auto-generated method stub
//                if (EditorInfo.IME_ACTION_SEARCH == actionId || EditorInfo.IME_ACTION_DONE == actionId) {
                    mSearchText = v.getText().toString();
                    if (TextUtils.isEmpty(mSearchText)) {
                        mGetDataTask.switchListViewMode(false);
                        mGetDataTask.RefreshList(MainTabActivity.mUser, false);
                    } else {
                        mGetDataTask.switchListViewMode(true);
                        mGetDataTask.RefreshSearchList(MainTabActivity.mUser, mSearchText, false);
                    }
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);  
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS); 
//                }
                return true;
            }
        });
        
        if (!mHasInit) {
            mGetDataTask.switchListViewMode(false);
            mGetDataTask.RefreshList(MainTabActivity.mUser, false);
            mHasInit = true;
        }
    }
    
    private void addHasReadContent(Content content) {
        for (Content c : mHuodongList) {
            if (c.cid == content.cid) {
                c.isRead = true;
                return;
            }
        }
        content.isRead = true;
        mHuodongList.add(content);
    }
    
    private Content hasReadContent(Content content) {
        boolean localFlag = false;
        for (Content c : mHuodongList) {
            if (c.cid == content.cid) {
                localFlag = c.isRead;
                break;
            }
        }
        if (localFlag) {
            content.isRead = true;
            return content;
        } else {
            return content;
        }
    }
    
    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState ) {
        // TODO Auto-generated method stub
        return inflater.inflate(R.layout.huodong_layout, null);
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (MainTabActivity.mUser != null) {
            mHuodongList = Utils.loadHuodongCidList(getActivity(), MainTabActivity.mUser);
        }
        mGetDataTask.notifyListView();
        mGetDataTask.notifySearchListView();
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }
    
    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        if (MainTabActivity.mUser != null) {
            Utils.saveHuodongCidList(getActivity(), MainTabActivity.mUser, mHuodongList);
        }
        if (mGetDataTask.mListView.getVisibility() == View.VISIBLE) {
            mIsShowSearch = false;
        } else {
            mIsShowSearch = true;
        }
    }



    private class HuodongAdapter extends BaseAdapter {
        private List<Content> innerList = new ArrayList<Content>();
        @Override
        public int getCount() {
            return innerList.size();
        }
        @Override
        public Object getItem( int position ) {
            return innerList.get(position);
        }
        @Override
        public long getItemId( int position ) {
            return position;
        }
        @Override
        public View getView( int position, View convertView, ViewGroup parent ) {
            // TODO Auto-generated method stub
            Content content = innerList.get(position);
            View v = null;
            int color;
            if (position % 2 == 0) {
                color = 0xFFE4E6E5;
            } else {
                color = 0xFFD6D6D6;
            }
            if (convertView == null) {
                ItemView iv = new ItemView(getActivity());
                iv.update(hasReadContent(content), color);
                v = iv;
            } else {
                try {
                    v = convertView;
                    ((ItemView) v).update(hasReadContent(content), color);
                } catch (Exception e) {
                    ItemView iv = new ItemView(getActivity());
                    iv.update(hasReadContent(content), color);
                    v = iv;
                }
            }
            return v;
        }
        public void notifyDataChange(List<Content> list) {
            this.innerList.clear();
            if (list != null) {
                innerList.addAll(list);
            }
            notifyDataSetChanged();
            
            if (mGetDataTask.mSearchListView.getVisibility() != View.GONE) {
                if (innerList.isEmpty()) {
                    mEmptyView.setVisibility(View.VISIBLE);
                } else {
                    mEmptyView.setVisibility(View.GONE);
                }
            }
            if (mGetDataTask.mListView.getVisibility() != View.GONE) {
                mEmptyView.setVisibility(View.GONE);
            }
        }
        
    }

    private static class GetDataTask {
        private int mPage;
        private int mSearchPage;
        private List<Content> mList = new ArrayList<Content>();
        private List<Content> mSearchList = new ArrayList<Content>();
        
        private RTPullListView mListView;
        private HuodongAdapter mAdapter;
        private boolean mIsListTaskFree = true;
        private RTPullListView mSearchListView;
        private HuodongAdapter mSearchAdapter;
        private boolean mIsSearchListTaskFree = true;
        
        private Context mContext;
        
        private TextView mEmptyView;
        
        public GetDataTask(HuodongAdapter adapter, HuodongAdapter searchAdapter) {
            this.mAdapter = adapter;
            this.mSearchAdapter = searchAdapter;
        }
        
        public void setContext(Context context) {
            this.mContext = context;
        }
        
        public void setListView(RTPullListView listview) {
            this.mListView = listview;
        }
        
        public void setSearchListView(RTPullListView searchListview) {
            this.mSearchListView = searchListview;
        }
        
        public void setEmptyView(TextView v) {
            this.mEmptyView = v;
        }
        
        public void switchListViewMode(boolean isSearchMode) {
            if (isSearchMode) {
                mListView.setVisibility(View.GONE);
                mSearchListView.setVisibility(View.VISIBLE);
            } else {
                mListView.setVisibility(View.VISIBLE);
                mSearchListView.setVisibility(View.GONE);
                mEmptyView.setVisibility(View.GONE);
            }
        }

        public void notifyListView() {
            mAdapter.notifyDataSetChanged();
        }
        
        public void notifySearchListView() {
            mSearchAdapter.notifyDataSetChanged();
        }
        
        public void RefreshList(User user, boolean isShowPullDownView) {
            if (user == null || !mIsListTaskFree) {
                if (isShowPullDownView) {
                    mListView.onRefreshComplete();
                }
                return;
            }
            mIsListTaskFree = false;
            try {
                mPage = 1;
                new GetDataTaskInternal().execute(
                        new Object[]{user, mPage, null, true, isShowPullDownView});
            } catch (RejectedExecutionException e) {
                if (isShowPullDownView) {
                    mListView.onRefreshComplete();
                }
                mIsListTaskFree = true;
            }
        }
        
        public void LoadMoreList(User user) {
            if (user == null) {
                return;
            }
            if (!mIsListTaskFree) {
                return;
            }
            mIsListTaskFree = false;
            try {
                new GetDataTaskInternal().execute(
                        new Object[]{user, mPage+1, null, false, false});
            } catch (RejectedExecutionException e) {
                mIsListTaskFree = true;
            }
        }
        
        public void RefreshSearchList(User user, String keyword, boolean isShowPullDownView) {
            if (user == null || !mIsSearchListTaskFree) {
                if (isShowPullDownView) {
                    mSearchListView.onRefreshComplete();
                }
                return;
            }
            mIsSearchListTaskFree = false;
            try {
                mSearchPage = 1;
                new GetDataTaskInternal().execute(
                        new Object[]{user, mSearchPage, keyword, true, isShowPullDownView});
            } catch (RejectedExecutionException e) {
                if (isShowPullDownView) {
                    mSearchListView.onRefreshComplete();
                }
                mIsSearchListTaskFree = true;
            }
        }
        
        public void LoadMoreSearchList(User user, String keyword) {
            if (user == null) {
                return;
            }
            if (!mIsSearchListTaskFree) {
                return;
            }
            mIsSearchListTaskFree = false;
            try {
                new GetDataTaskInternal().execute(
                        new Object[]{user, mSearchPage+1, keyword, false, false});
            } catch (RejectedExecutionException e) {
                mIsSearchListTaskFree = true;
            }
        }
        
        
        private class GetDataTaskInternal extends AsyncTask<Object, Void, List<Content>> {
            private boolean isSearch = false; 
            private boolean isRefresh;
            private boolean isShowPullDownView;
            
            @Override
            protected List<Content> doInBackground(Object... params) {
                User user = (User) params[0];
                int page = (Integer) params[1];
                String keyword = (String) params[2];
                if (TextUtils.isEmpty(keyword)) {
                    isSearch = false;
                } else {
                    isSearch = true;
                }
                isRefresh = (Boolean) params[3];
                isShowPullDownView = (Boolean) params[4];
                
                try {
                    ContentList contentList = NetDataSource.getInstance(mContext)
                            .getContentsList(user, 0,1, Constants.LIST_COUNT, page, keyword);
                    if (contentList != null && !contentList.mContentList.isEmpty()) {
                        return contentList.mContentList;
                    }
                } catch (ZhiDaoParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (ZhiDaoApiException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (ZhiDaoIOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(List<Content> result) {
                super.onPostExecute(result);
                if (isSearch) {
                    if (isRefresh) { //refresh
                        mSearchList.clear();
                        if (result == null || result.isEmpty()) {
//                            Log.d("net", "GetDataTaskInternal onPostExecute isSearch refresh result is empty");
                            mSearchListView.setSelection(0);
                            mSearchAdapter.notifyDataChange(mSearchList);
                        } else {
//                            Log.d("net", "GetDataTaskInternal onPostExecute isSearch refresh result not empty");
                            mSearchList.addAll(result);
                            mSearchListView.setSelection(0);
                            mSearchAdapter.notifyDataChange(mSearchList);
                        }
                    } else { //load more
                        if (result != null && !result.isEmpty()) {
//                            Log.d("net", "GetDataTaskInternal onPostExecute isSearch loadmore result not empty");
                            int oldSize = mSearchList.size();
                            if (oldSize == 0) {
                                mSearchListView.setSelection(0);
                            } else {
                                mSearchListView.setSelection(oldSize-1);
                            }
                            mSearchList.addAll(result);
                            mSearchPage++;
                            mSearchAdapter.notifyDataChange(mSearchList);
                        } else {
//                            Log.d("net", "GetDataTaskInternal onPostExecute isSearch loadmore result is empty");
                        }
                    }
                    if (isShowPullDownView) {
                        mSearchListView.onRefreshComplete();
                    }
                    mIsSearchListTaskFree = true;
                }
                else {
                    if (isRefresh) { //refresh
                        mList.clear();
                        if (result == null || result.isEmpty()) {
//                            Log.d("net", "GetDataTaskInternal onPostExecute not isSearch refresh result is empty");
                            mListView.setSelection(0);
                            mAdapter.notifyDataChange(mList);
                        } else {
//                            Log.d("net", "GetDataTaskInternal onPostExecute not isSearch refresh result not empty");
                            mList.addAll(result);
                            mListView.setSelection(0);
                            mAdapter.notifyDataChange(mList);
                        }
                    } else { //load more
                        if (result != null && !result.isEmpty()) {
//                            Log.d("net", "GetDataTaskInternal onPostExecute not isSearch loadmore result not empty");
                            int oldSize = mList.size();
                            if (oldSize == 0) {
                                mListView.setSelection(0);
                            } else {
                                mListView.setSelection(oldSize-1);
                            }
                            mList.addAll(result);
                            mPage++;
                            mAdapter.notifyDataChange(mList);
                        } else {
//                            Log.d("net", "GetDataTaskInternal onPostExecute not isSearch loadmore result is empty");
                        }
                    }
                    if (isShowPullDownView) {
                        mListView.onRefreshComplete();
                    }
                    mIsListTaskFree = true;
                }
            }
        }
        
    }
    
    
}
