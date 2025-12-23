package com.example.hifzaa4.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hifzaa4.R;
import com.example.hifzaa4.models.Post;

import java.util.ArrayList;
import java.util.List;

/**
 * PostAdapter - Custom RecyclerView adapter for displaying posts
 * Implements ViewHolder pattern and item click handling
 */
public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

  private List<Post> posts = new ArrayList<>();
  private OnPostClickListener clickListener;
  private OnPostLongClickListener longClickListener;
  private OnPostMenuClickListener menuClickListener;

  /**
   * Interface for item click events
   */
  public interface OnPostClickListener {
    void onPostClick(Post post, int position);
  }

  /**
   * Interface for long click events (context menu)
   */
  public interface OnPostLongClickListener {
    void onPostLongClick(Post post, int position, View view);
  }

  /**
   * Interface for popup menu click events
   */
  public interface OnPostMenuClickListener {
    void onEditClick(Post post, int position);

    void onDeleteClick(Post post, int position);

    void onShareClick(Post post, int position);

    void onFavoriteClick(Post post, int position);

    void onWebViewClick(Post post, int position);
  }

  /**
   * Set click listener
   */
  public void setOnPostClickListener(OnPostClickListener listener) {
    this.clickListener = listener;
  }

  /**
   * Set long click listener
   */
  public void setOnPostLongClickListener(OnPostLongClickListener listener) {
    this.longClickListener = listener;
  }

  /**
   * Set menu click listener
   */
  public void setOnPostMenuClickListener(OnPostMenuClickListener listener) {
    this.menuClickListener = listener;
  }

  /**
   * Update posts data
   */
  public void setPosts(List<Post> posts) {
    this.posts = posts != null ? posts : new ArrayList<>();
    notifyDataSetChanged();
  }

  /**
   * Get post at position
   */
  public Post getPostAt(int position) {
    if (position >= 0 && position < posts.size()) {
      return posts.get(position);
    }
    return null;
  }

  /**
   * Remove post at position
   */
  public void removePostAt(int position) {
    if (position >= 0 && position < posts.size()) {
      posts.remove(position);
      notifyItemRemoved(position);
    }
  }

  /**
   * Update post at position
   */
  public void updatePostAt(int position, Post post) {
    if (position >= 0 && position < posts.size()) {
      posts.set(position, post);
      notifyItemChanged(position);
    }
  }

  @NonNull
  @Override
  public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.item_post, parent, false);
    return new PostViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
    Post post = posts.get(position);
    holder.bind(post);
  }

  @Override
  public int getItemCount() {
    return posts.size();
  }

  /**
   * ViewHolder class for post items
   */
  class PostViewHolder extends RecyclerView.ViewHolder {

    private final TextView tvTitle;
    private final TextView tvBody;
    private final TextView tvUserId;
    private final TextView tvPostId;
    private final ImageView ivFavorite;
    private final ImageButton btnMore;

    PostViewHolder(@NonNull View itemView) {
      super(itemView);

      tvTitle = itemView.findViewById(R.id.tv_post_title);
      tvBody = itemView.findViewById(R.id.tv_post_body);
      tvUserId = itemView.findViewById(R.id.tv_user_id);
      tvPostId = itemView.findViewById(R.id.tv_post_id);
      ivFavorite = itemView.findViewById(R.id.iv_favorite);
      btnMore = itemView.findViewById(R.id.btn_more);

      // Item click
      itemView.setOnClickListener(v -> {
        int position = getAdapterPosition();
        if (clickListener != null && position != RecyclerView.NO_POSITION) {
          clickListener.onPostClick(posts.get(position), position);
        }
      });

      // Long click for context menu
      itemView.setOnLongClickListener(v -> {
        int position = getAdapterPosition();
        if (longClickListener != null && position != RecyclerView.NO_POSITION) {
          longClickListener.onPostLongClick(posts.get(position), position, v);
          return true;
        }
        return false;
      });

      // More button for popup menu
      btnMore.setOnClickListener(v -> {
        int position = getAdapterPosition();
        if (position != RecyclerView.NO_POSITION) {
          showPopupMenu(v, posts.get(position), position);
        }
      });
    }

    /**
     * Bind post data to views
     */
    void bind(Post post) {
      tvTitle.setText(post.getTitle());
      tvBody.setText(post.getBody());
      tvUserId.setText(itemView.getContext().getString(R.string.label_user_id, post.getUserId()));
      tvPostId.setText(itemView.getContext().getString(R.string.label_post_id, post.getId()));

      // Show favorite icon if favorited
      ivFavorite.setVisibility(post.isFavorite() ? View.VISIBLE : View.GONE);
    }

    /**
     * Show popup menu for item actions
     */
    private void showPopupMenu(View anchor, Post post, int position) {
      PopupMenu popup = new PopupMenu(anchor.getContext(), anchor);
      popup.inflate(R.menu.menu_popup);

      // Update favorite menu item text
      popup.getMenu().findItem(R.id.action_favorite)
          .setTitle(post.isFavorite() ? "Remove Favorite" : "Add Favorite");

      popup.setOnMenuItemClickListener(item -> {
        if (menuClickListener == null)
          return false;

        int itemId = item.getItemId();
        if (itemId == R.id.action_edit) {
          menuClickListener.onEditClick(post, position);
          return true;
        } else if (itemId == R.id.action_delete) {
          menuClickListener.onDeleteClick(post, position);
          return true;
        } else if (itemId == R.id.action_share) {
          menuClickListener.onShareClick(post, position);
          return true;
        } else if (itemId == R.id.action_favorite) {
          menuClickListener.onFavoriteClick(post, position);
          return true;
        } else if (itemId == R.id.action_view_web) {
          menuClickListener.onWebViewClick(post, position);
          return true;
        }
        return false;
      });

      popup.show();
    }
  }
}
