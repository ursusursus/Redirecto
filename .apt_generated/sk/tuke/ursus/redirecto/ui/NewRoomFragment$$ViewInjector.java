// Generated code from Butter Knife. Do not modify!
package sk.tuke.ursus.redirecto.ui;

import android.view.View;
import butterknife.ButterKnife.Finder;

public class NewRoomFragment$$ViewInjector {
  public static void inject(Finder finder, final sk.tuke.ursus.redirecto.ui.NewRoomFragment target, Object source) {
    View view;
    view = finder.findById(source, 2131296335);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131296335' for field 'mErrorTextView' was not found. If this view is optional add '@Optional' annotation.");
    }
    target.mErrorTextView = (android.widget.TextView) view;
    view = finder.findById(source, 2131296333);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131296333' for field 'mListView' was not found. If this view is optional add '@Optional' annotation.");
    }
    target.mListView = (android.widget.ListView) view;
    view = finder.findById(source, 2131296331);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131296331' for field 'mFilterEditText' was not found. If this view is optional add '@Optional' annotation.");
    }
    target.mFilterEditText = (android.widget.EditText) view;
    view = finder.findById(source, 2131296334);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131296334' for field 'mProgressBar' was not found. If this view is optional add '@Optional' annotation.");
    }
    target.mProgressBar = (android.widget.ProgressBar) view;
  }

  public static void reset(sk.tuke.ursus.redirecto.ui.NewRoomFragment target) {
    target.mErrorTextView = null;
    target.mListView = null;
    target.mFilterEditText = null;
    target.mProgressBar = null;
  }
}