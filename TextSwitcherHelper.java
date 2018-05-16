package leicher.textswitcher;

import android.content.Context;
import android.os.CountDownTimer;
import android.support.annotation.AnimRes;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import java.util.List;

/**
 * Created by leicher on 2018/5/15.
 * {@link android.widget.TextSwitcher} helper
 */
@SuppressWarnings("unused")
public class TextSwitcherHelper implements ViewSwitcher.ViewFactory{

    private static final long INTERVAL = 2000;

    private final TextSwitcher mSwitcher;
    private final Context mContext;
    private List<String> mData;
    private int mPosition;
    private long mTimeout;
    private long mInterval;
    private CountDownTimer mTimer;
    private Animation mInAnimation;
    private Animation mOutAnimation;
    private ParamsGenerator mGenerator;
    private boolean mFactorySet;

    private TextSwitcherHelper(TextSwitcher mSwitcher) {
        this.mSwitcher = mSwitcher;
        this.mContext = mSwitcher.getContext();
    }

    public static TextSwitcherHelper make(TextSwitcher switcher){
        return new TextSwitcherHelper(switcher);
    }

    /**
     *
     * @param mData 数据源
     * @return this
     */
    public TextSwitcherHelper data(List<String> mData) {
        this.mData = mData;
        return this;
    }

    /**
     *
     * @param timeout 超时时间 默认为永远
     * @return this
     */
    public TextSwitcherHelper timeout(long timeout) {
        this.mTimeout = timeout;
        if (mTimeout <= 0){
            mTimeout = Long.MAX_VALUE;
        }
        return this;
    }

    /**
     *
     * @param interval 时间间隔 default {@link #INTERVAL}
     * @return this
     */
    public TextSwitcherHelper interval(long interval) {
        this.mInterval = interval;
        return this;
    }

    /**
     *
     * @param mInAnimation view的进场动画
     * @return
     */
    public TextSwitcherHelper inAnimation(Animation mInAnimation) {
        this.mInAnimation = mInAnimation;
        return this;
    }

    public TextSwitcherHelper inAnimation(@AnimRes int anim){
        this.mInAnimation = AnimationUtils.loadAnimation(mContext, anim);
        return this;
    }

    /**
     *
     * @param mOutAnimation view 的出场动画
     * @return
     */
    public TextSwitcherHelper outAnimation(Animation mOutAnimation) {
        this.mOutAnimation = mOutAnimation;
        return this;
    }

    public TextSwitcherHelper outAnimation(@AnimRes int anim) {
        this.mOutAnimation = AnimationUtils.loadAnimation(mContext, anim);
        return this;
    }

    public TextSwitcherHelper generator(ParamsGenerator mGenerator) {
        this.mGenerator = mGenerator;
        return this;
    }

    @Override
    public View makeView() {
        TextView text = new TextView(mContext);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        if (mGenerator != null){
            params = mGenerator.generateLayoutParams(text, params);
        }
        if (text.getLayoutParams() != params){
            text.setLayoutParams(params);
        }
        return text;
    }

    /**
     * start with animation
     */
    public void start(int position){
        if (!mFactorySet){
            mSwitcher.setFactory(this);
            if (mOutAnimation == null){
                mOutAnimation = generateDefaultOutAnimation();
            }
            mSwitcher.setOutAnimation(mOutAnimation);

            if (mInAnimation == null){
                mInAnimation = generateDefaultInAnimation();
            }
            mSwitcher.setInAnimation(mInAnimation);
            mFactorySet = true;
        }
        if (mTimer == null){
            mTimer = new CountDownTimer(mTimeout <= 0 ? Long.MAX_VALUE : mTimeout, mInterval <= 0 ? 2000 : mInterval) {
                @Override
                public void onTick(long millisUntilFinished) {
                    if (mPosition < mData.size()){
                        mSwitcher.setText(mData.get(mPosition));
                    }
                    if (++mPosition >= mData.size()){
                        mPosition = 0;
                    }
                }

                @Override
                public void onFinish() {

                }
            };
        }

        if (mData != null && mData.size() > 0){
            mPosition = position >= mData.size() ? 0 : position;
            mTimer.start();
        }
    }

    public void start(){
        start(0);
    }

    /**
     * cancel
     */
    public void cancel(){
        if (mTimer != null)
            mTimer.cancel();
    }

    /**
     * 默认得退场动画
     * @return
     */
    public Animation generateDefaultOutAnimation(){
        Animation animation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0,
                Animation.RELATIVE_TO_PARENT, 0,
                Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT, -1);
        animation.setDuration(2000);
        return animation;
    }

    /**
     *
     * @return 默认得动画效果
     */
    public Animation generateDefaultInAnimation(){
        Animation animation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0,
                Animation.RELATIVE_TO_PARENT, 0,
                Animation.RELATIVE_TO_PARENT, 1, Animation.RELATIVE_TO_PARENT, 0);
        animation.setDuration(2000);
        return animation;
    }


    public interface ParamsGenerator{

        /**
         * @param text 为TextView 设置属性
         * @param params 生成 内部TextView的params
         * @return 可以直接返回
         */
        FrameLayout.LayoutParams generateLayoutParams(TextView text, FrameLayout.LayoutParams params);

    }

}
