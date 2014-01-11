// Generated code from Butter Knife. Do not modify!
package sk.tuke.ursus.redirecto;

import android.view.View;
import butterknife.ButterKnife.Finder;

public class RoomsArrayAdapter$ArrayViewHolder$$ViewInjector {
  public static void inject(Finder finder, final sk.tuke.ursus.redirecto.RoomsArrayAdapter.ArrayViewHolder target, Object source) {
    View view;
    view = finder.findById(source, 2131296342);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131296342' for field 'floor' was not found. If this view is optional add '@Optional' annotation.");
    }
    target.floor = (android.widget.TextView) view;
    view = finder.findById(source, 2131296341);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131296341' for field 'name' was not found. If this view is optional add '@Optional' annotation.");
    }
    target.name = (android.widget.TextView) view;
    view = finder.findById(source, 2131296343);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131296343' for field 'add' was not found. If this view is optional add '@Optional' annotation.");
    }
    target.add = (android.widget.ImageButton) view;
  }

  public static void reset(sk.tuke.ursus.redirecto.RoomsArrayAdapter.ArrayViewHolder target) {
    target.floor = null;
    target.name = null;
    target.add = null;
  }
}
