package com.arifcebe.rxandroid;

import android.content.Context;
import android.support.annotation.IntegerRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.net.ConnectException;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Observer;
import rx.Scheduler;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.label_text)
    TextView labelText;
    @BindView(R.id.btn_subscribe)
    Button btnSubscribe;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        context = MainActivity.this;
        final Subscription subscription = computeNumbersObservable
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(integerObserver);

        btnSubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subscription.unsubscribe();
            }
        });
    }

    Observer<Integer> integerObserver = new Observer<Integer>() {
        @Override
        public void onCompleted() {
            Log.d("PLAYGROUND", "onCompleted");
            labelText.setText("stream completed");
        }

        @Override
        public void onError(Throwable e) {
            Log.d("PLAYGROUND", "onError", e);
        }

        @Override
        public void onNext(Integer integer) {
            Log.d("PLAYGROUND", "onNext: " + integer);
            labelText.setText(integer.toString());
        }
    };

    Observable<Integer> computeNumbersObservable = Observable.create(new Observable.OnSubscribe<Integer>() {
        @Override
        public void call(Subscriber<? super Integer> subscriber) {

            int i = 0;

            while (true) {

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    subscriber.onError(e); // report error
                }

                subscriber.onNext(i++); // emit data

                if (i == 10) {
                    break;
                }

            }

            subscriber.onCompleted(); // indicate stream completion
        }
    });
}
