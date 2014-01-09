// Generated code from Butter Knife. Do not modify!
package sk.tuke.ursus.redirecto;

import android.view.View;
import butterknife.ButterKnife.Finder;

public class RoomsCursorAdapter$CursorViewHolder$$ViewInjector {
  public static void inject(Finder finder, final sk.tuke.ursus.redirecto.RoomsCursorAdapter.CursorViewHolder target, Object source) {
    View view;
    view = finder.findById(source, 2131296339);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131296339' for field 'overflow' was not found. If this view is optional add '@Optional' annotation.");
    }
    target.overflow = (android.widget.ImageButton) view;
    view = finder.findById(source, 2131296338);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131296338' for field 'view' was not found. If this view is optional add '@Optional' annotation.");
    }
    target.view = view;
    view = finder.findById(source, 2131296340);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131296340' for field 'name' was not found. If this view is optional add '@Optional' annotation.");
    }
    target.name = (android.widget.TextView) view;
    view = finder.findById(source, 2131296341);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131296341' for field 'floor' was not found. If this view is optional add '@Optional' annotation.");
    }
    target.floor = (android.widget.TextView) view;
  }

  public static void reset(sk.tuke.ursus.redirecto.RoomsCursorAdapter.CursorViewHolder target) {
    target.overflow = null;
    target.view = null;
    target.name = null;
    target.floor = null;
  }
}
