package com.lv.lv.volleyimageloaderdemo;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.util.LruCache;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;


public class MainActivity extends ActionBarActivity {
    /**
     * ImageLoader就是一个很好的例子。ImageLoader也可以用于加载网络上的图片，
     * 并且它的内部也是使用ImageRequest来实现的，不过ImageLoader明显要比ImageRequest更加高效，
     * 因为它不仅可以帮我们对图片进行缓存，还可以过滤掉重复的链接，避免重复发送请求。
     * <p/>
     * 1. 创建一个RequestQueue对象。
     * 2. 创建一个ImageLoader对象。
     * 3. 获取一个ImageListener对象。
     * 4. 调用ImageLoader的get()方法加载网络上的图片。
     */
    /*
    可以预见的效果就是，第一次加载的时候，会有默认图片过渡，第一次以后，都直接加载缓存好了的图片
     */

    private RequestQueue requestQueue;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestQueue = Volley.newRequestQueue(this);
        imageView = (ImageView) findViewById(R.id.imageView);
        //给予10M的缓存空间
        int maxSize = 10 * 1024 * 1024;
        bitmapLruCache = new LruCache<String, Bitmap>(maxSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                //一行所占的byte乘以bitmap的高度，就得到了图片区域所占的大小了
                return value.getRowBytes() * value.getHeight();
            }
        };
    }

    private LruCache<String, Bitmap> bitmapLruCache;

    public void onClick(View view) {
        //得到一个imageloader对象
        ImageLoader imageLoader = new ImageLoader(requestQueue, new ImageLoader.ImageCache() {
            @Override
            //从缓存中返回bitmap，如果缓存中没有以指定url为键的bitmap对象，则返回空值
            public Bitmap getBitmap(String url) {
                return bitmapLruCache.get(url);
            }

            @Override
            //如果缓存中没有，则将下载好的图片放入到缓存中
            public void putBitmap(String url, Bitmap bitmap) {
                if (getBitmap(url) == null) {
                    bitmapLruCache.put(url, bitmap);
                }
            }
        });
        //获取一个image的监听器 三个参数分别是view控件，默认图片和加载失败图片
        ImageLoader.ImageListener listener = imageLoader.getImageListener(imageView, R.drawable.ic_launcher, R.drawable.ic_launcher);
        //将监听器和图片下载链接加入到imageLoader中
        imageLoader.get("http://c.hiphotos.baidu.com/image/pic/item/2f738bd4b31c8701237508e1257f9e2f0608fff2.jpg", listener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
