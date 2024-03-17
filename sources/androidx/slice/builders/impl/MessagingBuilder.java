package androidx.slice.builders.impl;

import android.graphics.drawable.Icon;

public interface MessagingBuilder {

    public interface MessageBuilder {
        void addSource(Icon icon);

        void addText(CharSequence charSequence);

        void addTimestamp(long j);
    }

    void add(TemplateBuilderImpl templateBuilderImpl);

    TemplateBuilderImpl createMessageBuilder();
}
