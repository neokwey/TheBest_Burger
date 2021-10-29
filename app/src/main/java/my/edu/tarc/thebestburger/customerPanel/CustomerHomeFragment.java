package my.edu.tarc.thebestburger.customerPanel;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import my.edu.tarc.thebestburger.Domain.*;
import my.edu.tarc.thebestburger.Adapter.*;
import my.edu.tarc.thebestburger.*;

public class CustomerHomeFragment extends Fragment {
    private ArrayList<ProductDomain> popularList = new ArrayList<>();
    private ArrayList<ProductDomain> productList = new ArrayList<>();
    private ArrayList<ProductDomain> searchList = new ArrayList<>();
    private RecyclerView.Adapter adapter, adapter2, adapter3;
    private RecyclerView recyclerViewProductList, recyclerViewPopularList, recyclerViewSearchList;
    private DatabaseReference firebase, firebase2;
    private ConstraintLayout lay1,lay,lay3,lay4;
    private TextView txt1, txt2;

    @Nullable

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("Home");
        View v = inflater.inflate(R.layout.fragment_customerhome,null);

        txt1 = v.findViewById(R.id.textView10);
        txt1.setVisibility(View.VISIBLE);
        txt2 = v.findViewById(R.id.textView11);
        txt2.setVisibility(View.VISIBLE);
        lay1 = v.findViewById(R.id.layBurger);
        lay = v.findViewById(R.id.layDrink);
        lay3 = v.findViewById(R.id.laySides);
        lay4 = v.findViewById(R.id.lay2);
        lay4.setVisibility(View.INVISIBLE);
        adapter = new ProductAdapter(productList);
        adapter2 = new PopularAdapter(popularList);
        adapter3 = new SearchAdapter(searchList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager linearLayoutManager3 = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerViewSearchList = v.findViewById(R.id.recyclerView_cat);
        recyclerViewSearchList.setLayoutManager(linearLayoutManager3);
        recyclerViewSearchList.setAdapter(adapter3);
        recyclerViewProductList = v.findViewById(R.id.recyclerView3);
        recyclerViewProductList.setVisibility(View.VISIBLE);
        recyclerViewProductList.setLayoutManager(linearLayoutManager);
        recyclerViewProductList.setAdapter(adapter);
        recyclerViewPopularList = v.findViewById(R.id.recyclerView2);
        recyclerViewPopularList.setVisibility(View.VISIBLE);
        recyclerViewPopularList.setLayoutManager(linearLayoutManager2);
        recyclerViewPopularList.setAdapter(adapter2);
        getFood();
        getPopularFood();

        lay1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cat = "Burger";
                getCategoryFood(cat);
            }
        });
        lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cat = "Drinks";
                getCategoryFood(cat);
            }
        });
        lay3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cat = "Sides";
                getCategoryFood(cat);
            }
        });




        return v;
    }

    private void getFood() {
        productList.clear();
        firebase = FirebaseDatabase.getInstance().getReference("Product");
        firebase2 = FirebaseDatabase.getInstance().getReference("Product");
        firebase.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = (int)snapshot.getChildrenCount();
                String prod_ID = "";

                for(int i = count;i > 0;i--){
                    if(i<10){
                        prod_ID = "P000" + i;
                    }else if(i<100){
                        prod_ID = "P00" + i;
                    }else if(i<1000){
                        prod_ID = "P0" + i;
                    }else if(i<10000){
                        prod_ID = "P" + i;
                    }

                    final String Prod_ID = prod_ID;
                    firebase2.child(Prod_ID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            ProductDomain product = snapshot.getValue(ProductDomain.class);
                            productList.add(product);
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getPopularFood(){
        popularList.clear();
        firebase = FirebaseDatabase.getInstance().getReference("Popular");
        firebase2 = FirebaseDatabase.getInstance().getReference("Product");
        firebase.addListenerForSingleValueEvent(new ValueEventListener() {              //retriving from firebase

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {          //data stored to snapshot eg. P0001,P0002...
                int count = (int)snapshot.getChildrenCount();
                String prod_ID = "";

                for(int i = count;i > 0;i--){
                    if(i<10){
                        prod_ID = "P000" + i;
                    }else if(i<100){
                        prod_ID = "P00" + i;
                    }else if(i<1000){
                        prod_ID = "P0" + i;
                    }else if(i<10000){
                        prod_ID = "P" + i;
                    }

                    final String Prod_ID = prod_ID;

                    firebase2.child(Prod_ID).addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(@NonNull DataSnapshot dasnapshot) {
                            ProductDomain Popularprod = dasnapshot.getValue(ProductDomain.class);
                            String id = dasnapshot.child("product_ID").getValue().toString();
                            String name = dasnapshot.child("product_Name").getValue().toString();
                            String photo = dasnapshot.child("product_Photo").getValue().toString();
                            String price = dasnapshot.child("product_Price").getValue().toString();
                            popularList.add(Popularprod);
                            adapter2.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getCategoryFood(String Cat){
        searchList.clear();
        recyclerViewProductList.setVisibility(View.INVISIBLE);
        recyclerViewPopularList.setVisibility(View.INVISIBLE);
        txt1.setVisibility(View.INVISIBLE);
        txt2.setVisibility(View.INVISIBLE);
        lay4.setVisibility(View.VISIBLE);
        firebase = FirebaseDatabase.getInstance().getReference("Product");
        firebase2 = FirebaseDatabase.getInstance().getReference("Product");
        firebase.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = (int)snapshot.getChildrenCount();
                String prod_ID = "";

                for(int i = count;i > 0;i--){
                    if(i<10){
                        prod_ID = "P000" + i;
                    }else if(i<100){
                        prod_ID = "P00" + i;
                    }else if(i<1000){
                        prod_ID = "P0" + i;
                    }else if(i<10000){
                        prod_ID = "P" + i;
                    }

                    final String Prod_ID = prod_ID;
                    firebase2.child(Prod_ID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String checkCat = snapshot.child("product_Cat").getValue().toString();
                            if (Cat.equals(checkCat)){
                                ProductDomain product = snapshot.getValue(ProductDomain.class);
                                searchList.add(product);
                                adapter3.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}
