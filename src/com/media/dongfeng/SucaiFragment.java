package com.media.dongfeng;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RejectedExecutionException;

import org.apache.http.entity.mime.MinimalField;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.media.dongfeng.exception.ZhiDaoApiException;
import com.media.dongfeng.exception.ZhiDaoIOException;
import com.media.dongfeng.exception.ZhiDaoParseException;
import com.media.dongfeng.model.Content;
import com.media.dongfeng.model.ContentList;
import com.media.dongfeng.model.User;
import com.media.dongfeng.net.NetDataSource;
import com.media.dongfeng.utils.Constants;
import com.media.dongfeng.utils.Utils;
import com.media.dongfeng.view.CatTitleView;
import com.media.dongfeng.view.CatTitleView.OnDianjiClick;
import com.media.dongfeng.view.CatView;
import com.media.dongfeng.view.ItemView;
import com.media.dongfeng.view.RTPullListView;

public class SucaiFragment extends Fragment {

    private GetDataTask mGetDataTask;
    
    private EditText etSearchText;
    private ImageView mClearBtn;
    private TextView mEmptyView;
    private TextView mEmptyList;
    
    private String mSearchText;
    
    private ProgressBar mRrefresh;
    
    private ProgressBar mLoadMore;
    
    private boolean isMore = false;
    
    private List<Content> mSucaiList = new ArrayList<Content>();
    
    private SucaiAdapter mAdapter;
    private SucaiAdapter mSearchAdapter;
    
    private boolean mHasInit = false;
    private boolean mIsShowSearch = false;
    
    private Content content;
    
    private int fid;
    
    public SucaiFragment() {
		// TODO Auto-generated constructor stub
	}
    
    public SucaiFragment(Content content) {
    	this.content = content;
    	if(content == null){
    		fid = 0;
    	}else{
    		fid = content.cfid;
    	}
    	
        mAdapter = new SucaiAdapter();
        mSearchAdapter = new SucaiAdapter();
        mGetDataTask = new GetDataTask(mAdapter, mSearchAdapter);
       
    }
    
    @Override
    public void onCreate( Bundle savedInstanceState ) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
    }
    
    
   

    @Override
    public void onActivityCreated( Bundle savedInstanceState ) {
        super.onActivityCreated(savedInstanceState);
        mEmptyView = (TextView) getView().findViewById(R.id.empty);
        mEmptyList = (TextView) getView().findViewById(R.id.empty_list);
        mRrefresh = (ProgressBar) getView().findViewById(R.id.refreshPB);
        mLoadMore = (ProgressBar) getView().findViewById(R.id.loadMorePB);
        if (MainTabActivity.mUser != null) {
            mSucaiList = Utils.loadSucaiCidList(getActivity(), MainTabActivity.mUser);
        }
        
        RTPullListView mListView;
        RTPullListView mSearchListView;
        
        mListView = (RTPullListView) getView().findViewById(R.id.lvSucai);
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
            	if(scrollState == OnScrollListener.SCROLL_STATE_IDLE){
            		int pos = view.getLastVisiblePosition();
            		if(pos>20&&pos == view.getCount()-1){
            			mGetDataTask.switchListViewMode(false);
                        mGetDataTask.LoadMoreList(MainTabActivity.mUser);
                        
            		}
            	}
            }
            @Override
            public void onScroll( AbsListView view, int firstVisibleItem, int visibleItemCount,
                    int totalItemCount ) {
                // TODO Auto-generated method stub
//                if (TextUtils.isEmpty(mSearchText)) {
//                    if (totalItemCount > 0 && totalItemCount == firstVisibleItem + visibleItemCount) {
////                        Log.d("net", "mListView onScroll loadMore firstVisibleItem="+firstVisibleItem+"  visibleItemCount="+visibleItemCount+"    totalItemCount="+totalItemCount);
////                        mGetDataTask.switchListViewMode(false);
////                        mGetDataTask.LoadMoreList(MainTabActivity.mUser);
//                    }
//                }
            }
        });
        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick( AdapterView<?> parent, View view, int position, long id ) {
                // TODO Auto-generated method stub
                if (position <= 0) {
                    return;
                }
                onItemViewClick(mGetDataTask.mList,position-1);
//                Content content = mGetDataTask.mList.get(position-1);
//                addHasReadContent(content);
//                FragmentTransaction transation = getFragmentManager().beginTransaction();
//                transation.setCustomAnimations(R.anim.enter_right, 0, 0, 0);
//                SucaiHuodongDetailFragment fragment = new SucaiHuodongDetailFragment(content, true);
//                transation.replace(R.id.sucai_container, fragment, SucaiActivity.SUCAI_DETAIL_FRAGMENT);
//                transation.addToBackStack(SucaiActivity.SUCAI_DETAIL_FRAGMENT);
//                transation.commit();
            }
        });
        mListView.setAdapter(mAdapter);

        mSearchListView = (RTPullListView) getView().findViewById(R.id.lvSearchSucai);
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
            	
            	if(scrollState == OnScrollListener.SCROLL_STATE_IDLE){
            		int pos = view.getLastVisiblePosition();
            		if(pos>20&&pos == view.getCount()-1){
            			mGetDataTask.switchListViewMode(false);
                        mGetDataTask.LoadMoreList(MainTabActivity.mUser);
            		}
            	}
            }
            @Override
            public void onScroll( AbsListView view, int firstVisibleItem, int visibleItemCount,
                    int totalItemCount ) {
                // TODO Auto-generated method stub
//                if (!TextUtils.isEmpty(mSearchText)) {
//                if (totalItemCount > 0 && totalItemCount == firstVisibleItem + visibleItemCount) {
////                    Log.d("net", "mSearchListView onScroll loadMore firstVisibleItem="+firstVisibleItem+"  visibleItemCount="+visibleItemCount+"    totalItemCount="+totalItemCount);
//                    mGetDataTask.switchListViewMode(true);
//                    mGetDataTask.LoadMoreSearchList(MainTabActivity.mUser, mSearchText);
//                }
//                }
            }
            
        });
        mSearchListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick( AdapterView<?> parent, View view, int position, long id ) {
                // TODO Auto-generated method stub
                if (position <= 0) {
                    return;
                }
                onItemViewClick(mGetDataTask.mSearchList,position-1);
//                Content content = mGetDataTask.mSearchList.get(position-1);
//                addHasReadContent(content);
//                FragmentTransaction transation = getFragmentManager().beginTransaction();
//                transation.setCustomAnimations(R.anim.enter_right, 0, 0, 0);
//                SucaiHuodongDetailFragment fragment = new SucaiHuodongDetailFragment(content, true);
//                transation.replace(R.id.sucai_container, fragment, SucaiActivity.SUCAI_DETAIL_FRAGMENT);
//                transation.addToBackStack(SucaiActivity.SUCAI_DETAIL_FRAGMENT);
//                transation.commit();
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
        etSearchText.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(!hasFocus){
					InputMethodManager inputMethodManager = (InputMethodManager)  getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
				    inputMethodManager.hideSoftInputFromWindow(etSearchText.getWindowToken(), 0);
				}
				
			}
		});
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
                    mGetDataTask.switchListViewMode(false);
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
                    	mSearchText = mSearchText.replaceAll(" ", "-");
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
        if(fid == 0){
        	getView().findViewById(R.id.search_field).setVisibility(View.VISIBLE);
        }else{
        	View mBackBtn = getView().findViewById(R.id.back);
        	mBackBtn.setVisibility(View.VISIBLE);
        	mBackBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick( View v ) {
                    // TODO Auto-generated method stub
                    getFragmentManager().popBackStack();
                }
            });
        }
        
        
        
    }
    
    private void onItemViewClick(List<Content> contents,int position){
    	if(fid > 0 && position == 0){
    		return;
    	}
    	final Content content = contents.get(position);
    	
        FragmentTransaction transation = getFragmentManager().beginTransaction();
        transation.setCustomAnimations(R.anim.enter_right, 0, 0, 0);
        Fragment fragment = null;
        String newStack = null;
        if(fid == 0 && content.cfid>0){
        	new Thread(new Runnable() {
				
				@Override
				public void run() {
					try {
						NetDataSource.getInstance(getActivity()).ReadSucai(MainTabActivity.mUser, content.cfid, 1);
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
					
				}
			}).start();
        	fragment = new SucaiFragment(content);
        	newStack = SucaiActivity.SUCAI_FOLDER_FRAGMENT;
        }else{
        	fragment = new SucaiHuodongDetailFragment(content, true);
        	newStack = SucaiActivity.SUCAI_DETAIL_FRAGMENT;
        }
        
        transation.replace(R.id.sucai_container, fragment, newStack);
        transation.addToBackStack(newStack);
        transation.commit();
    }
    
    
    
//    private void addHasReadContent(Content content) {
//        for (Content c : mSucaiList) {
//            if (c.cid == content.cid) {
//                c.isRead = true;
//                return;
//            }
//        }
//        content.isRead = true;
//        mSucaiList.add(content);
//    }
    
//    private Content hasReadContent(Content content) {
//        boolean localFlag = false;
//        for (Content c : mSucaiList) {
//            if (c.cid == content.cid) {
//                localFlag = c.isRead;
//                break;
//            }
//        }
//        if (localFlag) {
//            content.isRead = true;
//            return content;
//        } else {
//            return content;
//        }
//    }
    
    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState ) {
        // TODO Auto-generated method stub
        return inflater.inflate(R.layout.sucai_layout, null);
    }

    boolean isNeedRefresh = true;
    public void needRefresh(boolean s){
    	isNeedRefresh = s;
    }
    
    public void hideLoading(){
    	mLoadMore.setVisibility(View.GONE);
    	mRrefresh.setVisibility(View.GONE);
    }
    
    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if(isNeedRefresh){
        	mGetDataTask.RefreshList(MainTabActivity.mUser, false);
        	isNeedRefresh = false;
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
            Utils.saveSucaiCidList(getActivity(), MainTabActivity.mUser, mSucaiList);
        }
        if (mGetDataTask.mListView.getVisibility() == View.VISIBLE) {
            mIsShowSearch = false;
        } else {
            mIsShowSearch = true;
        }
    }



    private class SucaiAdapter extends BaseAdapter {
    	
    	private boolean mSendMailTaskFree = true;
    	
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
        
        private class SendMailTask extends AsyncTask<Void, Void, Boolean> {

            
            protected Boolean doInBackground( Void... args ) {
                try {
                    if (NetDataSource.getInstance(getActivity()).sendMail(MainTabActivity.mUser, content.cfid,1)) {
                        return true;
                    }
                } catch (ZhiDaoIOException e) {
                } catch (ZhiDaoApiException e) {
                } catch (ZhiDaoParseException e) {
                    
                }
                return false;
            }
            
            @Override
            protected void onPostExecute( Boolean result ) {
                super.onPostExecute(result);
                if (result) {
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), "发送成功", 0).show();
                    }
                } else {
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), "发送失败", 0).show();
                    }
                }
                mSendMailTaskFree = true;
            }
        }
        
        @Override
        public View getView( int position, View convertView, ViewGroup parent ) {
            // TODO Auto-generated method stub
            final Content content = innerList.get(position);
            View v = null;
            int color;
            if (position % 2 == 0) {
                color = 0xFFE4E6E5;
            } else {
                color = 0xFFD6D6D6;
            }
            if(content.isCatTitle && fid > 0){
            	CatTitleView iv = new CatTitleView(getActivity());
            	iv.setOnDianjiClick(new OnDianjiClick() {
					
					@Override
					public void onclick(View view) {
						if(!mSendMailTaskFree){
							return;
						}
						mSendMailTaskFree = false;
						new SendMailTask().execute();
						
					}
				});
            	iv.update(SucaiFragment.this.content);
            	v=iv;
            }else if(content.cid == 0  && content.cfid != 0){
            	CatView iv = new CatView(getActivity());
                iv.update(content, color);
                v = iv;
            }else if(content.cid>0){
            	ItemView iv = new ItemView(getActivity());
                iv.update(content, color);
                v = iv;
            }
            
            
//            if (convertView == null) {
//                
//                
//            } else {
//                try {
//                    v = convertView;
//                    ((ItemView) v).update(hasReadContent(content), color);
//                } catch (Exception e) {
//                    ItemView iv = new ItemView(getActivity());
//                    iv.update(hasReadContent(content), color);
//                    v = iv;
//                }
//            }
            return v;
        }
        public void notifyDataChange(List<Content> list) {
        	
            this.innerList.clear();
            if(fid > 0){
        		Content content = SucaiFragment.this.content;
        		
        		content.isCatTitle = true;
        		list.add(0, content);
        	}
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
            }else{
            	if(innerList == null || innerList.size() == 1){
            		mEmptyList.setVisibility(View.VISIBLE);
            	}else{
            		mEmptyList.setVisibility(View.GONE);
            	}
            }
            if (mGetDataTask.mListView.getVisibility() != View.GONE) {
                mEmptyView.setVisibility(View.GONE);
            }
        }
        
    }

    private class GetDataTask {
        private int mPage;
        private int mSearchPage;
        private List<Content> mList = new ArrayList<Content>();
        private List<Content> mSearchList = new ArrayList<Content>();
        
        private RTPullListView mListView;
        private SucaiAdapter mAdapter;
        private boolean mIsListTaskFree = true;
        private RTPullListView mSearchListView;
        private SucaiAdapter mSearchAdapter;
        private boolean mIsSearchListTaskFree = true;
        
        private Context mContext;
        
        private TextView mEmptyView;
        
        public GetDataTask(SucaiAdapter adapter, SucaiAdapter searchAdapter) {
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
            protected void onPreExecute() {
                // TODO Auto-generated method stub
                super.onPreExecute();
            }

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
                if(isRefresh && !isShowPullDownView){
                	SucaiFragment.this.getActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							mRrefresh.setVisibility(View.VISIBLE);
							
						}
					});
                	
                }
                if(!isSearch && page > 1 && isMore){
                	SucaiFragment.this.getActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							mLoadMore.setVisibility(View.VISIBLE);
							
						}
					});
                }
                
                try {
                    ContentList contentList = NetDataSource.getInstance(mContext)
                            .getContentsList(user,fid,0, Constants.LIST_COUNT, page, keyword);
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
                hideLoading();
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
                	if(isRefresh && result!= null && result.size() == Constants.LIST_COUNT){
                		isMore = true;
                	}else if(!isRefresh&&result!= null && mList!=null && result.size() - mList.size() == Constants.LIST_COUNT){
                		isMore = true;
                	}else{
                		isMore = false;
                	}
                	
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
