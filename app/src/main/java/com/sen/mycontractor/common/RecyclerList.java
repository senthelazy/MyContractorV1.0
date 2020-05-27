package com.sen.mycontractor.common;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.transition.Slide;
import android.view.Gravity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.callback.ItemDragAndSwipeCallback;
import com.chad.library.adapter.base.listener.OnItemDragListener;
import com.chad.library.adapter.base.listener.OnItemSwipeListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sen.mycontractor.R;

import com.sen.mycontractor.customer.ContractorProfiles;
import com.sen.mycontractor.customer.adapter.EstimatesAdapter;
import com.sen.mycontractor.customer.adapter.MyRequestsListRecyclerViewAdapter;

import com.sen.mycontractor.customer.widget.FilterImageView;
import com.sen.mycontractor.data.Estimate;
import com.sen.mycontractor.data.Project;
import com.sen.mycontractor.technician.adapter.TechEstimatesAdapter;
import com.sen.mycontractor.technician.adapter.TechRequestsListAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RecyclerList extends AppCompatActivity {

    private MyRequestsListRecyclerViewAdapter customerAdapter;
    private TechRequestsListAdapter techAdapter;
    private EstimatesAdapter customerEstimatesAdapter;
    private TechEstimatesAdapter techEstimatesAdapter;
    private ItemTouchHelper itemTouchHelper;
    private ItemDragAndSwipeCallback itemDragAndSwipeCallback;
    private DatabaseReference databaseRef;
    private FirebaseUser mUser;
    private StorageReference storageRef;
    private List<Project> projects;
    private List<Estimate> estimates;
    private Estimate mEstimate;
    private Project mProject;
    private String status;
    private int mFirstPageItemCount = 3;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.list_title)
    TextView title;
    @BindView(R.id.no_estimate)
    TextView noEstimate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_list);
        setupWindowAnimations();
        ButterKnife.bind(this);
        databaseRef = FirebaseDatabase.getInstance().getReference();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        storageRef = FirebaseStorage.getInstance().getReference();
        status = getIntent().getStringExtra("status");
        mProject = getIntent().getParcelableExtra("Project");
        mEstimate = getIntent().getParcelableExtra("Estimate");
        projects = new ArrayList<>();
        estimates = new ArrayList<>();
        showProgressDialog();
        if (status.equals("customer")) {
            myRequestsList();
        } else if (status.equals("technician")) {
            techRequestsList();
        } else if (status.equals("EstimatesCustomer")) {
            customerEstimates();
        } else if (status.equals("EstimatesTechnician")) {
            technicianEstimates();
        }
    }


    private void customerEstimates() {
        estimates.clear();
        title.setText(getResources().getString(R.string.estimates_from_tech_list));
        databaseRef.child("Estimates").orderByChild("projectId").equalTo(Integer.toString(mProject.getID()))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                Estimate estimate = ds.getValue(Estimate.class);
                                estimates.add(estimate);
                            }
                            fadeOutProgressDialog();
                            customerEstimatesAdapter = new EstimatesAdapter(estimates);
                            customerEstimatesAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_RIGHT);
                            customerEstimatesAdapter.setNotDoAnimationCount(mFirstPageItemCount);
                            recyclerView.setLayoutManager(new LinearLayoutManager(RecyclerList.this));
                            customerEstimatesAdapter.isFirstOnly(false);
                            customerEstimatesAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(RecyclerList.this);
                                    Intent intent = new Intent(RecyclerList.this, ContractorProfiles.class);
                                    intent.putExtra("status", "EstimatesCustomer");
                                    intent.putExtra("Estimate", estimates.get(position));
                                    startActivity(intent, options.toBundle());
                                }
                            });
                            recyclerView.setAdapter(customerEstimatesAdapter);
                        } else {
                            fadeOutProgressDialog();
                            noEstimate.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }

    private void technicianEstimates() {
        estimates.clear();
        title.setText(getResources().getString(R.string.my_estimates_list));
        databaseRef.child("Estimates").orderByChild("techId").equalTo(mUser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                Estimate estimate = ds.getValue(Estimate.class);
                                estimates.add(estimate);
                            }
                            fadeOutProgressDialog();
                            techEstimatesAdapter = new TechEstimatesAdapter(estimates);
                            recyclerView.setLayoutManager(new LinearLayoutManager(RecyclerList.this));
                            final Paint paint = new Paint();
                            paint.setAntiAlias(true);
                            paint.setTextSize(100);
                            paint.setColor(Color.RED);
                            OnItemSwipeListener itemSwipeListener = new OnItemSwipeListener() {
                                @Override
                                public void onItemSwipeStart(final RecyclerView.ViewHolder viewHolder, final int pos) {
                                    BaseViewHolder holder = ((BaseViewHolder) viewHolder);
                                }

                                @Override
                                public void clearView(RecyclerView.ViewHolder viewHolder, final int pos) {
                                    BaseViewHolder holder = ((BaseViewHolder) viewHolder);
                                }

                                @Override
                                public void onItemSwiped(RecyclerView.ViewHolder viewHolder, final int pos) {
                                    BaseViewHolder holder = ((BaseViewHolder) viewHolder);
                                    databaseRef.child("Estimates").child(mEstimate.getEstimateId()).removeValue();
                                }
                                @Override
                                public void onItemSwipeMoving(Canvas canvas, RecyclerView.ViewHolder viewHolder, float dX, float dY, boolean isCurrentlyActive) {
                                    canvas.drawColor(ContextCompat.getColor(RecyclerList.this, R.color.colorLightBlue));
                                    canvas.drawText("Delete", 0, 240, paint);
                                }
                            };
                            itemDragAndSwipeCallback = new ItemDragAndSwipeCallback(techEstimatesAdapter);
                            itemTouchHelper = new ItemTouchHelper(itemDragAndSwipeCallback);
                            itemTouchHelper.attachToRecyclerView(recyclerView);
                            itemDragAndSwipeCallback.setSwipeMoveFlags(ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
                            techEstimatesAdapter.enableSwipeItem();
                            techEstimatesAdapter.setOnItemSwipeListener(itemSwipeListener);
                            techEstimatesAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                                    databaseRef.child("Projects").child(estimates.get(position).getProjectId())
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    Project project=dataSnapshot.getValue(Project.class);
                                                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(RecyclerList.this);
                                                    Intent intent0 = new Intent(RecyclerList.this, RequestDetails.class);
                                                    intent0.putExtra("status", "singleProject");
                                                    intent0.putExtra("Project",project );
                                                    startActivity(intent0, options.toBundle());
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });

                                }
                            });
                            recyclerView.setAdapter(techEstimatesAdapter);
                        } else {
                            fadeOutProgressDialog();
                            noEstimate.setText("You have not sent any estimate yet.");
                            noEstimate.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }


    private void myRequestsList() {
        this.setTitle(mUser.getDisplayName() + "'s Requests List");
        projects.clear();
        title.setText(getResources().getString(R.string.my_requests_list));
        databaseRef.child("Projects").orderByChild("customerUid").equalTo(mUser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                Project project = ds.getValue(Project.class);
                                projects.add(project);
                            }
                            fadeOutProgressDialog();
                            customerAdapter = new MyRequestsListRecyclerViewAdapter(RecyclerList.this, projects);
                            recyclerView.setLayoutManager(new LinearLayoutManager(RecyclerList.this));
                            OnItemDragListener itemDragListener = new OnItemDragListener() {
                                @Override
                                public void onItemDragStart(RecyclerView.ViewHolder viewHolder, int pos) {
                                    BaseViewHolder holder = (BaseViewHolder) viewHolder;

                                }

                                @Override
                                public void onItemDragMoving(RecyclerView.ViewHolder source, final int from, RecyclerView.ViewHolder target, int to) {

                                }

                                @Override
                                public void onItemDragEnd(RecyclerView.ViewHolder viewHolder, int pos) {
                                    BaseViewHolder holder = ((BaseViewHolder) viewHolder);
                                }
                            };
                            final Paint paint = new Paint();
                            paint.setAntiAlias(true);
                            paint.setTextSize(100);
                            paint.setColor(Color.RED);
                            OnItemSwipeListener itemSwipeListener = new OnItemSwipeListener() {
                                @Override
                                public void onItemSwipeStart(final RecyclerView.ViewHolder viewHolder, final int pos) {
                                    BaseViewHolder holder = ((BaseViewHolder) viewHolder);

                                }

                                @Override
                                public void clearView(RecyclerView.ViewHolder viewHolder, final int pos) {
                                    BaseViewHolder holder = ((BaseViewHolder) viewHolder);
                                }

                                @Override
                                public void onItemSwiped(RecyclerView.ViewHolder viewHolder, final int pos) {
                                    BaseViewHolder holder = ((BaseViewHolder) viewHolder);
                                    databaseRef.child("Projects").child(Integer.toString(projects.get(pos).getID())).removeValue();
                                    for (int i = 0; i < projects.get(pos).getImagesOriginalUri().size(); i++) {
                                        Project project = projects.get(pos);
                                        storageRef.child("Images/" + project.getOriginalLastPathSegment().get(i)).delete();
                                        storageRef.child("ThumbImages/" + project.getThumbnailLastPathSegment().get(i)).delete();
                                    }
                                    storageRef.child("Videos/" + projects.get(pos).getVideoLastPathSegment()).delete();
                                }

                                @Override
                                public void onItemSwipeMoving(Canvas canvas, RecyclerView.ViewHolder viewHolder, float dX, float dY, boolean isCurrentlyActive) {
                                    canvas.drawColor(ContextCompat.getColor(RecyclerList.this, R.color.colorLightBlue));
                                    canvas.drawText("Delete", 0, 240, paint);

                                }
                            };
                            itemDragAndSwipeCallback = new ItemDragAndSwipeCallback(customerAdapter);
                            itemTouchHelper = new ItemTouchHelper(itemDragAndSwipeCallback);
                            itemTouchHelper.attachToRecyclerView(recyclerView);
                            itemDragAndSwipeCallback.setSwipeMoveFlags(ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
                            customerAdapter.enableSwipeItem();
                            customerAdapter.setOnItemSwipeListener(itemSwipeListener);
                            customerAdapter.enableDragItem(itemTouchHelper);
                            customerAdapter.setOnItemDragListener(itemDragListener);
                            customerAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
                                @Override
                                public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                                    switch (view.getId()) {
                                        case R.id.details_icon:
                                            ActivityOptions option0 = ActivityOptions.makeSceneTransitionAnimation(RecyclerList.this);
                                            Intent intent0 = new Intent(RecyclerList.this, RequestDetails.class);
                                            intent0.putExtra("status", "customer");
                                            intent0.putExtra("Project", projects.get(position));
                                            startActivity(intent0, option0.toBundle());
                                            break;
                                        case R.id.details:
                                            ActivityOptions option1 = ActivityOptions.makeSceneTransitionAnimation(RecyclerList.this);
                                            Intent intent1 = new Intent(RecyclerList.this, RequestDetails.class);
                                            intent1.putExtra("status", "customer");
                                            intent1.putExtra("Project", projects.get(position));
                                            startActivity(intent1, option1.toBundle());
                                            break;
                                        case R.id.estimates:
                                            ActivityOptions option2 = ActivityOptions.makeSceneTransitionAnimation(RecyclerList.this);
                                            Intent intent2 = new Intent(RecyclerList.this, RecyclerList.class);
                                            intent2.putExtra("status", "EstimatesCustomer");
                                            intent2.putExtra("Project", projects.get(position));
                                            startActivity(intent2, option2.toBundle());
                                            break;
                                        case R.id.estimates_icon:
                                            ActivityOptions option3 = ActivityOptions.makeSceneTransitionAnimation(RecyclerList.this);
                                            Intent intent3 = new Intent(RecyclerList.this, RecyclerList.class);
                                            intent3.putExtra("status", "EstimatesCustomer");
                                            intent3.putExtra("Project", projects.get(position));
                                            startActivity(intent3, option3.toBundle());
                                            break;

                                    }

                                }
                            });
                            recyclerView.setAdapter(customerAdapter);
                            customerAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

                                }
                            });
                        }else{
                            fadeOutProgressDialog();
                            noEstimate.setText("You have not posted any request yet.");
                            noEstimate.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void techRequestsList() {
        projects.clear();
        databaseRef.child("Users").child("Technicians")
                .child(mUser.getUid()).child("cityName").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String techLocation = dataSnapshot.getValue(String.class);
                title.setText("Requests List of " + techLocation);
                databaseRef.child("Projects").orderByChild("location").equalTo(techLocation)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                        Project project = ds.getValue(Project.class);
                                        projects.add(project);
                                    }
                                    fadeOutProgressDialog();
                                    techAdapter = new TechRequestsListAdapter(projects);
                                    techAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_LEFT);
                                    techAdapter.setNotDoAnimationCount(mFirstPageItemCount);
                                    recyclerView.setLayoutManager(new LinearLayoutManager(RecyclerList.this));
                                    techAdapter.isFirstOnly(false);
                                    techAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                                            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(RecyclerList.this);
                                            Intent intent = new Intent(RecyclerList.this, RequestDetails.class);
                                            intent.putExtra("status", "technician");
                                            intent.putExtra("Project", projects.get(position));
                                            startActivity(intent, options.toBundle());
                                        }
                                    });
                                    recyclerView.setAdapter(techAdapter);
                                }else{
                                    fadeOutProgressDialog();
                                    noEstimate.setText("There's no request yet.");
                                    noEstimate.setVisibility(View.VISIBLE);
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError error) {
                            }
                        });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void showProgressDialog() {
        progressBar.setAlpha(1f);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#ffffff"), PorterDuff.Mode.SRC_IN);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void fadeOutProgressDialog() {
        progressBar.animate().alpha(0f).setDuration(10).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

            }
        }).start();
    }

    @OnClick(R.id.backToAccountIb)
    public void back() {
        super.onBackPressed();
    }

    private void setupWindowAnimations() {
        Slide slideTransition = new Slide();
        slideTransition.setSlideEdge(Gravity.LEFT);
        slideTransition.setDuration(500);
        getWindow().setReenterTransition(slideTransition);
        getWindow().setExitTransition(slideTransition);
        getWindow().setAllowReturnTransitionOverlap(false);
        Slide enterTransition = new Slide();
        enterTransition.setSlideEdge(Gravity.RIGHT);
        enterTransition.setDuration(500);
        getWindow().setEnterTransition(enterTransition);
        getWindow().setAllowEnterTransitionOverlap(true);
    }

}
