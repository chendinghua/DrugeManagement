// Generated code from Butter Knife. Do not modify!
package com.example.druge.ui.fragment;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.Button;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.example.druge.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class PickUpTaskListFragment_ViewBinding implements Unbinder {
  private PickUpTaskListFragment target;

  private View view2131689977;

  private View view2131689978;

  @UiThread
  public PickUpTaskListFragment_ViewBinding(final PickUpTaskListFragment target, View source) {
    this.target = target;

    View view;
    view = Utils.findRequiredView(source, R.id.btn_pick_up_task_commit, "field 'btnPickUpTaskCommit' and method 'onViewClicked'");
    target.btnPickUpTaskCommit = Utils.castView(view, R.id.btn_pick_up_task_commit, "field 'btnPickUpTaskCommit'", Button.class);
    view2131689977 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.btn_pick_up_task_rollback, "field 'btnPickUpTaskRollback' and method 'onViewClicked'");
    target.btnPickUpTaskRollback = Utils.castView(view, R.id.btn_pick_up_task_rollback, "field 'btnPickUpTaskRollback'", Button.class);
    view2131689978 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    target.sfPickUpTaskRefresh = Utils.findRequiredViewAsType(source, R.id.sf_pick_up_task_refresh, "field 'sfPickUpTaskRefresh'", SwipeRefreshLayout.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    PickUpTaskListFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.btnPickUpTaskCommit = null;
    target.btnPickUpTaskRollback = null;
    target.sfPickUpTaskRefresh = null;

    view2131689977.setOnClickListener(null);
    view2131689977 = null;
    view2131689978.setOnClickListener(null);
    view2131689978 = null;
  }
}
