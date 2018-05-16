# TextSwitcherHelper
help you to use TextSwitcher in Android

you can copy this java file to your project And use it like

TextSwitcherHelper.make(switcher).data(texts).interval(2000).generator(new TextSwitcherHelper.ParamsGenerator() {
            @Override
            public FrameLayout.LayoutParams generateLayoutParams(TextView text, FrameLayout.LayoutParams params) {
                text.setTextColor(Color.BLACK);
                text.setGravity(Gravity.CENTER);
                params.height = ViewGroup.LayoutParams.MATCH_PARENT;
                return params;
            }
        }).start();

