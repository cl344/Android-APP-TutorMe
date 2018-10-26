package compsci290.edu.duke.tutor.chat;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.leancloud.chatkit.view.LCIMDividerItemDecoration;
import compsci290.edu.duke.tutor.R;
import de.greenrobot.event.EventBus;

/**
 * Contact Page
 */
public class ContactFragment extends Fragment {

    protected SwipeRefreshLayout refreshLayout;
    protected RecyclerView recyclerView;

    private MembersAdapter itemAdapter;
    LinearLayoutManager layoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.contact_fragment, container, false);

        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.contact_fragment_srl_list);
        recyclerView = (RecyclerView) view.findViewById(R.id.contact_fragment_rv_list);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new LCIMDividerItemDecoration(getActivity()));
        itemAdapter = new MembersAdapter();
        recyclerView.setAdapter(itemAdapter);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshMembers();
            }
        });

        EventBus.getDefault().register(this);
        return view;
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshMembers();
    }

    private void refreshMembers() {
        itemAdapter.setMemberList(CustomUserProvider.getInstance().getAllUsers());
        itemAdapter.notifyDataSetChanged();
        refreshLayout.setRefreshing(false);
    }

    /**
     * Handle LetterView sent MemberLetterEvent
     * Get position through MembersAdapter
     */
    public void onEvent(MemberLetterEvent event) {
        Character targetChar = Character.toLowerCase(event.letter);
        if (itemAdapter.getIndexMap().containsKey(targetChar)) {
            int index = itemAdapter.getIndexMap().get(targetChar);
            if (index > 0 && index < itemAdapter.getItemCount()) {
                layoutManager.scrollToPositionWithOffset(index, 0);
            }
        }
    }
}