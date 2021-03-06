/*
 * This file is part of Butter.
 *
 * Butter is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Butter is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Butter. If not, see <http://www.gnu.org/licenses/>.
 */

package butter.droid.ui.media.detail.show.about;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import butter.droid.R;
import butter.droid.base.providers.media.models.Show;
import butter.droid.fragments.dialog.SynopsisDialogFragment;
import butter.droid.ui.media.detail.show.ShowDetailFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.squareup.picasso.Picasso;
import javax.inject.Inject;

public class ShowDetailAboutFragment extends Fragment implements ShowDetailAboutView {

    private static final String ARG_SHOW = "butter.droid.ui.media.detail.show.about.ShowDetailAboutFragment.show";

    @Inject ShowDetailAboutPresenter presenter;

    @BindView(R.id.title) TextView tvTitle;
    @BindView(R.id.meta) TextView tvMetaData;
    @BindView(R.id.synopsis) TextView tvSynopsis;
    @BindView(R.id.rating) RatingBar rbRating;
    @BindView(R.id.read_more) Button mReadMore;
    @BindView(R.id.info_buttons) LinearLayout vInfoButtons;
    @BindView(R.id.magnet) @Nullable ImageButton mOpenMagnet;
    @Nullable @BindView(R.id.cover_image) ImageView coverImage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((ShowDetailFragment) getParentFragment()).getComponent()
                .showDetailAboutBuilder()
                .showDetailAboutModule(new ShowDetailAboutModule(this))
                .build()
                .inject(this);

        Show show = getArguments().getParcelable(ARG_SHOW);

        presenter.onCreate(show);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_about, container, false);

        if (container != null) {
            view.setMinimumHeight(container.getMinimumHeight());
        }

        return view;
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        if (mOpenMagnet != null) {
            mOpenMagnet.setVisibility(View.GONE);
        }

        presenter.onViewCreated();
    }

    @OnClick(R.id.read_more) public void openReadMore() {
        presenter.readMoreClicked();
    }
    
    @Override public void displayTitle(String title) {
        tvTitle.setText(title);
    }

    @Override public void hideRating() {
        rbRating.setVisibility(View.GONE);
    }

    @Override public void displayRating(int rating, String cd) {
        rbRating.setProgress(rating);
        rbRating.setContentDescription(cd);
        rbRating.setVisibility(View.VISIBLE);
    }

    @Override public void displayMetaData(String metaData) {
        tvMetaData.setText(metaData);
    }

    @Override public void displaySynopsis(String synopsis) {
        tvSynopsis.setVisibility(View.VISIBLE);
        tvSynopsis.setText(synopsis);
        tvSynopsis.post(new Runnable() {
            @Override
            public void run() {
                boolean ellipsized = false;
                Layout layout = tvSynopsis.getLayout();
                if (layout == null) return;
                int lines = layout.getLineCount();
                if (lines > 0) {
                    int ellipsisCount = layout.getEllipsisCount(lines - 1);
                    if (ellipsisCount > 0) {
                        ellipsized = true;
                    }
                }
                vInfoButtons.setVisibility(ellipsized ? View.VISIBLE : View.GONE);
            }
        });
    }

    @Override public void hideSynopsis() {
        tvSynopsis.setVisibility(View.GONE);
        vInfoButtons.setVisibility(View.GONE);
    }

    @Override public void openSynopsisDialog(String synopsis) {
        // TODO: 3/20/17 Make it nicer (part of SynopsisDialogFragment refactor)
        if (getFragmentManager().findFragmentByTag("overlay_fragment") != null) {
            return;
        }

        SynopsisDialogFragment synopsisDialogFragment = new SynopsisDialogFragment();
        Bundle b = new Bundle();
        b.putString("text", synopsis);
        synopsisDialogFragment.setArguments(b);
        synopsisDialogFragment.show(getFragmentManager(), "overlay_fragment");
    }

    @Override
    public void displayImage(final String image) {
        if (coverImage != null) {
            Picasso.with(coverImage.getContext()).load(image).into(coverImage);
        }
    }

    public static ShowDetailAboutFragment newInstance(Show show) {
        Bundle args = new Bundle();
        args.putParcelable(ARG_SHOW, show);

        ShowDetailAboutFragment fragment = new ShowDetailAboutFragment();
        fragment.setArguments(args);
        return fragment;
    }

}
