package androidx.slice.builders.impl;

import android.app.PendingIntent;
import android.net.Uri;
import androidx.core.graphics.drawable.IconCompat;
import androidx.core.util.Pair;
import androidx.slice.Slice;
import androidx.slice.SliceSpec;
import androidx.slice.builders.GridRowBuilder;
import androidx.slice.builders.SliceAction;
import com.android.launcher3.LauncherSettings;
import java.util.ArrayList;
import java.util.List;

public class GridRowBuilderListV1Impl extends TemplateBuilderImpl {
    private SliceAction mPrimaryAction;

    public GridRowBuilderListV1Impl(ListBuilderV1Impl listBuilderV1Impl, GridRowBuilder gridRowBuilder) {
        super(listBuilderV1Impl.createChildBuilder(), (SliceSpec) null);
        if (gridRowBuilder.getLayoutDirection() != -1) {
            setLayoutDirection(gridRowBuilder.getLayoutDirection());
        }
        if (gridRowBuilder.getDescription() != null) {
            setContentDescription(gridRowBuilder.getDescription());
        }
        if (gridRowBuilder.getSeeMoreIntent() != null) {
            setSeeMoreAction(gridRowBuilder.getSeeMoreIntent());
        } else if (gridRowBuilder.getSeeMoreCell() != null) {
            setSeeMoreCell(gridRowBuilder.getSeeMoreCell());
        }
        if (gridRowBuilder.getPrimaryAction() != null) {
            setPrimaryAction(gridRowBuilder.getPrimaryAction());
        }
        for (GridRowBuilder.CellBuilder addCell : gridRowBuilder.getCells()) {
            addCell(addCell);
        }
    }

    public void apply(Slice.Builder builder) {
        builder.addHints("horizontal");
        SliceAction sliceAction = this.mPrimaryAction;
        if (sliceAction != null) {
            sliceAction.setPrimaryAction(builder);
        }
    }

    public void addCell(GridRowBuilder.CellBuilder cellBuilder) {
        CellBuilderImpl cellBuilderImpl = new CellBuilderImpl(this);
        cellBuilderImpl.fillFrom(cellBuilder);
        cellBuilderImpl.apply(getBuilder());
    }

    public void setSeeMoreCell(GridRowBuilder.CellBuilder cellBuilder) {
        CellBuilderImpl cellBuilderImpl = new CellBuilderImpl(this);
        cellBuilderImpl.fillFrom(cellBuilder);
        cellBuilderImpl.getBuilder().addHints("see_more");
        cellBuilderImpl.apply(getBuilder());
    }

    public void setSeeMoreAction(PendingIntent pendingIntent) {
        getBuilder().addSubSlice(new Slice.Builder(getBuilder()).addHints("see_more").addAction(pendingIntent, new Slice.Builder(getBuilder()).build(), (String) null).build());
    }

    public void setPrimaryAction(SliceAction sliceAction) {
        this.mPrimaryAction = sliceAction;
    }

    public void setContentDescription(CharSequence charSequence) {
        getBuilder().addText(charSequence, "content_description", new String[0]);
    }

    public void setLayoutDirection(int i) {
        getBuilder().addInt(i, "layout_direction", new String[0]);
    }

    public static final class CellBuilderImpl extends TemplateBuilderImpl {
        private PendingIntent mContentIntent;

        CellBuilderImpl(GridRowBuilderListV1Impl gridRowBuilderListV1Impl) {
            super(gridRowBuilderListV1Impl.createChildBuilder(), (SliceSpec) null);
        }

        public void fillFrom(GridRowBuilder.CellBuilder cellBuilder) {
            if (cellBuilder.getCellDescription() != null) {
                setContentDescription(cellBuilder.getCellDescription());
            }
            if (cellBuilder.getContentIntent() != null) {
                setContentIntent(cellBuilder.getContentIntent());
            }
            List<Object> objects = cellBuilder.getObjects();
            List<Integer> types = cellBuilder.getTypes();
            List<Boolean> loadings = cellBuilder.getLoadings();
            for (int i = 0; i < objects.size(); i++) {
                int intValue = types.get(i).intValue();
                if (intValue == 0) {
                    addText((CharSequence) objects.get(i), loadings.get(i).booleanValue());
                } else if (intValue == 1) {
                    addTitleText((CharSequence) objects.get(i), loadings.get(i).booleanValue());
                } else if (intValue == 2) {
                    Pair pair = (Pair) objects.get(i);
                    addImage((IconCompat) pair.first, ((Integer) pair.second).intValue(), loadings.get(i).booleanValue());
                }
            }
        }

        private CellBuilderImpl(Uri uri) {
            super(new Slice.Builder(uri), (SliceSpec) null);
        }

        private void addText(CharSequence charSequence) {
            addText(charSequence, false);
        }

        private void addText(CharSequence charSequence, boolean z) {
            getBuilder().addText(charSequence, (String) null, z ? new String[]{"partial"} : new String[0]);
        }

        private void addTitleText(CharSequence charSequence) {
            addTitleText(charSequence, false);
        }

        private void addTitleText(CharSequence charSequence, boolean z) {
            getBuilder().addText(charSequence, (String) null, z ? new String[]{"partial", LauncherSettings.Favorites.TITLE} : new String[]{LauncherSettings.Favorites.TITLE});
        }

        private void addImage(IconCompat iconCompat, int i) {
            addImage(iconCompat, i, false);
        }

        private void addImage(IconCompat iconCompat, int i, boolean z) {
            ArrayList arrayList = new ArrayList();
            if (i != 0) {
                arrayList.add("no_tint");
            }
            if (i == 2) {
                arrayList.add("large");
            }
            if (z) {
                arrayList.add("partial");
            }
            getBuilder().addIcon(iconCompat, (String) null, (List<String>) arrayList);
        }

        private void setContentIntent(PendingIntent pendingIntent) {
            this.mContentIntent = pendingIntent;
        }

        private void setContentDescription(CharSequence charSequence) {
            getBuilder().addText(charSequence, "content_description", new String[0]);
        }

        public void apply(Slice.Builder builder) {
            getBuilder().addHints("horizontal");
            PendingIntent pendingIntent = this.mContentIntent;
            if (pendingIntent != null) {
                builder.addAction(pendingIntent, getBuilder().build(), (String) null);
            } else {
                builder.addSubSlice(getBuilder().build());
            }
        }
    }
}
