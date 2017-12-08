package com.mycompany.grifon.mm_pre_alpha.ui.music;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.mycompany.grifon.mm_pre_alpha.R;
import com.mycompany.grifon.mm_pre_alpha.data.PlainUser;
import com.mycompany.grifon.mm_pre_alpha.data.Post;
import com.mycompany.grifon.mm_pre_alpha.data.Profile;
import com.mycompany.grifon.mm_pre_alpha.engine.firebase.FirebaseAuthHelper;
import com.mycompany.grifon.mm_pre_alpha.engine.firebase.FirebasePathHelper;
import com.mycompany.grifon.mm_pre_alpha.engine.firebase.FirebaseUtils;
import com.mycompany.grifon.mm_pre_alpha.engine.music.Player;
import com.mycompany.grifon.mm_pre_alpha.ui.LoginActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class RecyclerViewAdapterPosts extends RecyclerView.Adapter<RecyclerViewAdapterPosts.ViewHolder> {

    private Map<String, Post> originalData;//= new LinkedHashMap<>();
    private final List<Post> mData;//= new LinkedHashMap<>();
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    private String uuid;
    // for reposts
    // true = my profile, false = not mine
    private boolean profyleType;
    // true = ProfileActivity, false = NewsActivity
    private boolean activityType;

    private FirebaseUtils firebaseUtils;
    //private static Profile myProfile = FirebaseAuthHelper.getInstance().getProfile();

    // player
    private Player player;
    private Context context;

    // data is passed into the constructor
    public RecyclerViewAdapterPosts(Context context, LinkedHashMap<String, Post> data, String uuid, boolean profileType, boolean activityType, FirebaseUtils firebaseUtils) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = new ArrayList<>(data.values());
        Collections.sort(mData, new Comparator<Post>() {
            public int compare(Post post1, Post post2) {
                return post1.compareTo(post2);
            }
        });

        this.context = context;
        originalData = data;
        //        Collections.reverse(data);

        this.player = LoginActivity.player;
        this.uuid = uuid;
        this.profyleType = profileType;
        this.activityType = activityType;

        this.firebaseUtils = firebaseUtils;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recycler_post_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    // binds the data to the textView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //Log.d("MY LOG:", "POSTS SET: " + mData);
        try {
            if (!mData.isEmpty()) {
                //notifyDataSetChanged();
                //PlainUser me = FirebaseAuthHelper.getInstance().getProfile().toPlain();

                Log.d("MY LOG:", "POSTS SET is correct ");

                String songName = mData.get(position).getSong().getName();
                String postText = mData.get(position).getText();
                String authorName = null;
                Post currentPost = mData.get(position);
                // чтобы в NewsActivity не писать имя автора, если это наш пост
                if (!uuid.equals(currentPost.getAuthor().getUuid())) {
                    authorName = currentPost.getAuthor().getName();
                    holder.tv_authorName.setText(authorName);
                }

                //btnv_repost.setVisibility(View.INVISIBLE);

                //Log.d("MY LOG:", "Author name: " + authorName);
                int likes = currentPost.getSong().getLikes();
                holder.tv_songName.setText(songName);
                holder.tv_post_text.setText(postText);
                holder.tv_likes.setText(Integer.toString(likes));

            } else {
                Log.d("MY LOG:", "POSTS SET is empty or null");
                holder.tv_songName.setText("no songs");
                holder.tv_post_text.setText("no posts");
                holder.tv_likes.setText("0");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv_songName;
        TextView tv_likes;
        TextView tv_post_text;
        TextView tv_authorName;
        Button btnv_play;
        Button btnv_pause;
        Button btnv_repost;
        Button btn_del;
        CheckBox checkBox;

        public ViewHolder(View itemView) {
            super(itemView);

            tv_songName = (TextView) itemView.findViewById(R.id.tv_recycler_item);
            tv_likes = (TextView) itemView.findViewById(R.id.tv_likes);
            tv_post_text = (TextView) itemView.findViewById(R.id.tv_text_post_item);
            tv_authorName = (TextView) itemView.findViewById(R.id.tv_author_name);
            btnv_play = (Button) itemView.findViewById(R.id.btn_play);
            btnv_pause = (Button) itemView.findViewById(R.id.btn_pause);
            btnv_repost = (Button) itemView.findViewById(R.id.btn_repost);
            btn_del = (Button) itemView.findViewById(R.id.btn_del);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkBox_like);
            //checkBox.setOnCheckedChangeListener(checkBoxListener);

            // if my profile - no reposts
            // and you can del posts
            if (profyleType) {//если я зашёл на свой профиль
                btnv_repost.setVisibility(View.INVISIBLE);
            } else {//если я зашёл к другому человеку в профиль
                btn_del.setVisibility(View.INVISIBLE);
            }

            // нереализованный функционал
            tv_likes.setVisibility(View.INVISIBLE);
            checkBox.setVisibility(View.INVISIBLE);

            btn_del.setOnClickListener(this);
            btnv_play.setOnClickListener(this);
            btnv_pause.setOnClickListener(this);
            btnv_repost.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        //@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.btn_play) {
                // play song
                try {
                    player.startPlayback(mData.get(getAdapterPosition()).getSong().getUrl());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (view.getId() == R.id.btn_pause) {
                // stop song
                try {
                    player.stopPlayback();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (view.getId() == R.id.btn_repost) {
                try {
                    Post post = mData.get(getAdapterPosition());
                    if (FirebaseAuthHelper.getInstance().getProfile() != null) {
                        PlainUser me = FirebaseAuthHelper.getInstance().getProfile().toPlain();

                        // if NewsActivity
                        if (!activityType) {
                            // if author not I
                            if (!me.getUuid().equals(post.getAuthor().getUuid())) {
                                final String time = String.valueOf(System.currentTimeMillis());
                                final Post repostedPost = new Post(post.getText(), post.getSong(), me, time, UUID.randomUUID().toString());
                                FirebasePathHelper.getInstance().writeNewPostDB(me.getUuid(), repostedPost);

                                Profile myProfile = FirebaseAuthHelper.getInstance().getProfile();
                                Map<String, PlainUser> subscribers = myProfile.getSubscribers();
                                for (String s : subscribers.keySet()) {
                                    FirebasePathHelper.getInstance().writeNewPostDB(s, repostedPost);
                                }
                            } else {
                                // работает не так, как хотелось бы
                                //btnv_repost.setVisibility(View.INVISIBLE);
                            }
                        } else {
                            // else - ProfileActivity
                            final String time = String.valueOf(System.currentTimeMillis());
                            final Post repostedPost = new Post(post.getText(), post.getSong(), me, time, UUID.randomUUID().toString());
                            FirebasePathHelper.getInstance().writeNewPostDB(me.getUuid(), repostedPost);

                            Profile myProfile = FirebaseAuthHelper.getInstance().getProfile();
                            Map<String, PlainUser> subscribers = myProfile.getSubscribers();
                            for (String s : subscribers.keySet()) {
                                FirebasePathHelper.getInstance().writeNewPostDB(s, repostedPost);
                            }
                        }
                    }
                    Toast.makeText(context, "Reposted", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (view.getId() == R.id.btn_del) {
                try {
                    Profile myProfile = FirebaseAuthHelper.getInstance().getProfile();
                    Map<String, PlainUser> subscribers = myProfile.getSubscribers();
                    Map<String, Post> allPost = myProfile.getPosts();
                    String uuid = mData.get(getAdapterPosition()).getUuid();
                    for (Iterator<Post> iter = mData.iterator(); iter.hasNext(); ) {
                        final Post removedPost = iter.next();
                        if (removedPost.getUuid().equals(uuid) /*&& removedPost.getAuthor().getUuid().equals(myProfile.getUuid())*/) {
                            iter.remove();
                            notifyItemRemoved(getAdapterPosition());
                            allPost.remove(removedPost.getUuid());
                            for (String s : subscribers.keySet()) {
                                FirebasePathHelper.getInstance().deletePostDB(s, removedPost.getAuthor().getUuid(), removedPost);
                            }
                            break;
                        }
                    }

                /*final Map<String, Post> myPosts = myProfile.getPosts();
                final Map<String, Post> hisPosts = profile.getPosts();
                final Map<String, Post> hisOwnPosts = new HashMap<>();
                for (String s : hisPosts.keySet()) {
                    final Post post = hisPosts.get(s);
                    if (post.getAuthor().getUuid().equals(profile.getUuid())) {
                        hisOwnPosts.put(s, post);
                        myPosts.remove(s);
                    }
                }*/

                    FirebasePathHelper.getInstance().updatePosts(allPost);
                    //dataSource.remove(index); // remember to remove it from your adapter data source
                    //notifyItemRemoved(index);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

/*
        // like it!
        CompoundButton.OnCheckedChangeListener checkBoxListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                // ставим лайк
                    Post post = mData.get(getAdapterPosition());
                    //int likes = Integer.parseInt(post.getSong().getLikes());
                    int likes = post.getSong().getLikes();
                    likes++;
                    FirebasePathHelper.addLikeDB(uuid, post.getTimestamp(), likes);

                } else {
                // убираем лайк
                    Post post = mData.get(getAdapterPosition());
                    //int likes = Integer.parseInt(post.getSong().getLikes());
                    int likes = post.getSong().getLikes();
                    likes--;
                    FirebasePathHelper.addLikeDB(uuid, post.getTimestamp(), likes);
                }
            }
        };
        */
    }


    // convenience method for getting data at click position
    public Post getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
        //void onItemClick(int position);
    }

}
