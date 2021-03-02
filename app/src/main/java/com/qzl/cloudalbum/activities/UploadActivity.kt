package com.qzl.cloudalbum.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.qzl.cloudalbum.R
import com.qzl.cloudalbum.other.UserHelper
import com.qzl.cloudalbum.other.netErr
import com.qzl.cloudalbum.other.showToast
import kotlinx.android.synthetic.main.activity_upload.*
import kotlinx.android.synthetic.main.title_layout.*
import kotlinx.coroutines.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.lang.Exception
import java.net.ConnectException
import kotlin.concurrent.thread

class UploadActivity : AppCompatActivity() {

    private var parentPath: String = ""
    private var pic: File? = null
    private var picName: String = ""
    private var picSize: Long = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)

        supportActionBar?.hide()
        titleMeun.visibility = View.GONE
        titleText.text = "选择上传图片"

        parentPath = intent.getStringExtra("thisPath")!!

        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        startActivityForResult(intent, 2)

        rechoise_Bt.setOnClickListener {
            startActivityForResult(intent, 2)
        }

        commit_Bt.setOnClickListener {

            if (pic != null) {
                /*//获取扩展名
                val options = BitmapFactory.Options()
                options.inJustDecodeBounds = true
                BitmapFactory.decodeFile(pic?.path, options)
                val type = options.outMimeType
                val extension = when (type) {
                    "image/jpeg" -> ".jpg"
                    "image/png" -> ".png"
                    "image/gif" -> ".gif"
                    else -> ".jpg"
                }*/

                if (picSize >= 128L * 1024L * 1024L) {
                    Log.i("size", picSize.toString())
                    "图片过大".showToast(this)

                } else {

                    Log.i("size", picSize.toString())
                    val myDialog: AlertDialog.Builder = AlertDialog.Builder(this)
                    val editText = EditText(this)
                    myDialog.setTitle("请输入文件名").setView(editText)
                    editText.setText(picName)

                    myDialog.setPositiveButton("确定") { dialog, which ->
                        val newName = editText.text.toString()

                        if (!UserHelper.nameInLaw(newName)) {
                            "文件名不合法".showToast(this)
                        } else {
                            //发起上传
                            lifecycleScope.launch {
                                val requestfile =
                                    RequestBody.create(
                                        MediaType.parse("multipart/form-data"),
                                        pic!!
                                    )

                                val file = MultipartBody.Part.createFormData(
                                    "file",
                                    editText.text.toString(),
                                    requestfile
                                )

                                try {

                                    //加载转圈圈
                                    imageloding.visibility = View.VISIBLE
                                    Glide.with(this@UploadActivity).load(R.mipmap.loding)
                                        .into(imageloding)

                                    if (UserHelper.uploadPic(parentPath, file)) {
                                        "上传成功".showToast(this@UploadActivity)
                                        this@UploadActivity.finish()
                                    } else {
                                        "上传失败".showToast(this@UploadActivity)
                                        imageloding.visibility = View.GONE
                                    }
                                } catch (e: ConnectException) {
                                    imageloding.visibility = View.GONE
                                    e.printStackTrace()
                                    //无网络提示
                                    netErr(this@UploadActivity)
                                } catch (e: Exception) {
                                    imageloding.visibility = View.GONE
                                    e.printStackTrace()
                                    //测试提示
                                    "其他异常".showToast(this@UploadActivity)
                                }


                            }
                        }


                    }

                    myDialog.setNegativeButton("取消") { dialog, which ->
                        "取消".showToast(this)
                    }
                    myDialog.show()
                }

            } else "请选择图片".showToast(this)
        }

        picToUpload_Iv.setOnClickListener {
            startActivityForResult(intent, 2)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when {
            requestCode == 2 && resultCode == Activity.RESULT_OK -> {
                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        val uri = data?.data

                        uri?.let {

                            pic =
                                Glide.with(this@UploadActivity).downloadOnly().load(it).submit()
                                    .get()

                            val cursor = contentResolver.query(it, null, null, null, null)
                            cursor?.moveToFirst()

                            picName = cursor?.getString(2)!!
                            picSize = cursor.getString(5).toLong()

                            cursor.close()

                        }
                    }
                    delay(100)
                    Glide.with(this@UploadActivity).load(pic).into(picToUpload_Iv)
                }
            }
        }
    }
}
