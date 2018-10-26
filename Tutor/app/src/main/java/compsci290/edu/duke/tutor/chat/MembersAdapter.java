package compsci290.edu.duke.tutor.chat;


import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cn.leancloud.chatkit.LCChatKitUser;


public class MembersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    /**
     * all member list
     */
    private List<MemberItem> memberList = new ArrayList<MemberItem>();

    private Map<Character, Integer> indexMap = new HashMap<Character, Integer>();

    Collator cmp = Collator.getInstance(Locale.SIMPLIFIED_CHINESE);

    public MembersAdapter() {}

    /**
     * setting member list
     * will reorder through letter
     */
    public void setMemberList(List<LCChatKitUser> userList) {
        memberList.clear();
        if (null != userList) {
            for (LCChatKitUser user : userList) {
                MemberItem item = new MemberItem();
                item.lcChatKitUser = user;
                item.sortContent = PinyinHelper.convertToPinyinString(user.getUserName(), "", PinyinFormat.WITHOUT_TONE);
                memberList.add(item);
            }
        }
        Collections.sort(memberList, new SortChineseName());
        updateIndex();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ContactItemHolder(parent.getContext(), parent);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ((ContactItemHolder) holder).bindData(memberList.get(position).lcChatKitUser);
    }

    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    @Override
    public int getItemCount() {
        return memberList.size();
    }

    /**
     * index Map
     */
    public Map<Character, Integer> getIndexMap() {
        return indexMap;
    }

    /**
     * updating index Map
     */
    private void updateIndex() {
        Character lastCharcter = '#';
        indexMap.clear();
        for (int i = 0; i < memberList.size(); i++) {
            Character curChar = Character.toLowerCase(memberList.get(i).sortContent.charAt(0));
            if (!lastCharcter.equals(curChar)) {
                indexMap.put(curChar, i);
            }
            lastCharcter = curChar;
        }
    }

    public class SortChineseName implements Comparator<MemberItem> {

        @Override
        public int compare(MemberItem str1, MemberItem str2) {
            if (null == str1) {
                return -1;
            }
            if (null == str2) {
                return 1;
            }
            if (cmp.compare(str1.sortContent, str2.sortContent)>0){
                return 1;
            }else if (cmp.compare(str1.sortContent, str2.sortContent)<0){
                return -1;
            }
            return 0;
        }
    }

    public static class MemberItem {
        public LCChatKitUser lcChatKitUser;
        public String sortContent;
    }
}