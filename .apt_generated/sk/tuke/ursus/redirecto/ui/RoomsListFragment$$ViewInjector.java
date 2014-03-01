// Generated code from Butter Knife. Do not modify!
package sk.tuke.ursus.redirecto.ui;

import android.view.View;
import butterknife.ButterKnife.Finder;

public class RoomsListFragment$$ViewInjector {
  public static void inject(Finder finder, final sk.tuke.ursus.redirecto.ui.RoomsListFragment target, Object source) {
    View view;
    view = finder.findById(source, 2131296338);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131296338' for field 'mBoardingButton' and method 'goToNewRoomActivity' was not found. If this view is optional add '@Optional' annotation.");
    }
    target.mBoardingButton = (android.widget.Button) view;
    view.setOnClickListener(
      new android.view.View.OnClickListener() {
        @Override public void onClick(
          android.view.View p0
        ) {
          target.goToNewRoomActivity();
        }
      });
    view = finder.findById(source, 2131296336);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131296336' for field 'mErrorTextView' was not found. If this view is optional add '@Optional' annotation.");
    }
    target.mErrorTextView = (android.widget.TextView) view;
    view = finder.findById(source, 2131296335);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131296335' for field 'mProgressBar' was not found. If this view is optional add '@Optional' annotation.");
    }
    target.mProgressBar = (android.widget.ProgressBar) view;
    view = finder.findById(source, 2131296337);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131296337' for field 'mGridView' was not found. If this view is optional add '@Optional' annotation.");
    }
    target.mGridView = (android.widget.GridView) view;
  }

  public static void reset(sk.tuke.ursus.redirecto.ui.RoomsListFragment target) {
    target.mBoardingButton = null;
    target.mErrorTextView = null;
    target.mProgressBar = null;
    target.mGridView = null;
  }
}
