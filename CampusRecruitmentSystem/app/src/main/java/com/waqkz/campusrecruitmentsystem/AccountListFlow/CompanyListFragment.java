package com.waqkz.campusrecruitmentsystem.AccountListFlow;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.waqkz.campusrecruitmentsystem.AccountCreationFlow.SignUp;
import com.waqkz.campusrecruitmentsystem.AccountDetailFlow.AccountDetailActivity;
import com.waqkz.campusrecruitmentsystem.AccountInfoFlow.CompanyInfo;
import com.waqkz.campusrecruitmentsystem.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class CompanyListFragment extends Fragment implements CompanyListRecyclerAdapter.itemClickCallback{

    private RecyclerView companyRecyclerView;
    private CompanyListRecyclerAdapter companyListRecyclerAdapter;
    private ArrayList<UserList> companyArrayList;
    private UserList users;

    public CompanyListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_company_list, container, false);

        AccountListActivity.toolBarText.setText(getString(R.string.company_list_info));

        attachingWidgets(rootView);

        companyRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(),2);
        companyRecyclerView.setLayoutManager(layoutManager);

        companyArrayList = new ArrayList<UserList>();

        attachingComponents();

        companyListRecyclerAdapter = new CompanyListRecyclerAdapter(companyArrayList, getActivity());
        companyRecyclerView.setAdapter(companyListRecyclerAdapter);
        companyListRecyclerAdapter.setOnItemClickCallback(this);

        return rootView;
    }

    public void attachingWidgets(View view){

        companyRecyclerView = (RecyclerView) view.findViewById(R.id.company_card_list);
    }

    public void attachingComponents(){

        FirebaseDatabase.getInstance().getReference()
                .child(getString(R.string.campus))
                .child(getString(R.string.company_type))
                .child(getString(R.string.company_login_info))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()){

                            for (DataSnapshot data: dataSnapshot.getChildren()) {

                                final SignUp signUp = data.getValue(SignUp.class);

                                FirebaseDatabase.getInstance().getReference()
                                        .child(getString(R.string.campus))
                                        .child(getString(R.string.company_type))
                                        .child(getString(R.string.company_info))
                                        .child(signUp.getUUID())
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {

                                                if (dataSnapshot.exists()){

                                                    CompanyInfo companyInfo = dataSnapshot.getValue(CompanyInfo.class);

                                                    users = new UserList(companyInfo.getCompanyURL(),
                                                            companyInfo.getCompanyName(),
                                                            companyInfo.getCompanyWebPage(),
                                                            signUp.getEmail());

                                                    companyArrayList.add(users);
                                                    companyListRecyclerAdapter.notifyDataSetChanged();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {
                                            }
                                        });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    @Override
    public void onItemClick(int position) {

        UserList userList  = companyArrayList.get(position);

        Intent intent = new Intent(getActivity(), AccountDetailActivity.class);
        intent.putExtra("memberType", AccountListActivity.membershipType);
        startActivity(intent);
    }
}
