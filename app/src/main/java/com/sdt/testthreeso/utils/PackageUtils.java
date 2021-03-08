package com.sdt.testthreeso.utils;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ServiceInfo;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.content.FileProvider;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Locale;

/**
 * 应用安装工具包
 * <p>Title: PackageUtils.java</p>
 * <p>Description:</p>
 *
 * @author ljf++
 * @date 2016年10月20日 下午7:21:53
 */
public class PackageUtils {
    private final static String TAG = PackageUtils.class.getSimpleName();

    /**
     * 安装类型:
     * <br>正常安装({@link #INSTALLING_NORMAL})
     * <br>静默安装({@link #INSTALLING_BACKGROUND})
     */
    public final static String EXTRA_INSTALLING_TYPE = "extra_installing_type";
    /**
     * APK的安装路径
     */
    public final static String EXTRA_APK_PATH = "extra_apk_path";
    /**
     * 调用安装处理结果：准备安装前失败: 1. 静默安装没有权限，并且调用系统安装对话框失败。
     */
    public final static String EXTRA_INSTALL_PREPARE = "extra_install_prepare";
    /**
     * 安装结果{@link #INSTALL_SUCCEEDED}等等
     */
    public final static String EXTRA_INSTALL_RESULT = "extra_install_result";

    /**
     * 安装类型: 正常安装，调用系统安装对话框
     */
    public final static int INSTALLING_NORMAL = 100;
    /**
     * 安装类型: 静默安装, 需要系统权限
     */
    public final static int INSTALLING_BACKGROUND = 101;
    /**
     * adb安装
     */
    public final static int INSTALLING_ADB = 102;
    /**
     * OEM厂商后门安装
     */
    public final static int INSTALLING_OEM_BACKDOOR = 103;

    /**
     * Installation return code
     * <br>install success.
     */
    public final static int INSTALL_SUCCEEDED = 1;
    /**
     * Installation return code
     * <br>the package is already installed
     */
    public final static int INSTALL_FAILED_ALREADY_EXISTS = -1;
    /**
     * Installation return code
     * <br>the package archive file is invalid
     */
    public final static int INSTALL_FAILED_INVALID_APK = -2;
    /**
     * installation return code
     * <br>the URI passed in is invalid
     */
    public final static int INSTALL_FAILED_INVALID_URI = -3;
    /**
     * installation return code
     * <br>the package manager service found that the device didn't have enough storage space to install the app.
     */
    public final static int INSTALL_FAILED_INSFUFFICIENT_STORAGE = -4;
    /**
     * Installation return code
     * <br> a package is already installed with the same name.
     */
    public final static int INSTALL_FAILED_DUPLICATE_PACKAGE = -5;
    /**
     * Installation return code
     * <br> the requested shared user does not exist.
     */
    public final static int INSTALL_FAILED_NOT_SHARED_USER = -6;
    /**
     * Installation return code
     * <br> a previously installed package of the same name has a different signature than the new package (and the old
     * package's data was not removed).
     */
    public final static int INSTALL_FAILED_UPDATE_INCOMPATIBLE = -7;
    /**
     * Installation return code
     * <br> the new package is requested a shared user which is already installed on the device and does not have matching
     * signature.
     */
    public final static int INSTALL_FAILED_SHARED_USER_INCOMPATIBLE = -8;
    /**
     * Installation return code
     * <br> the new package uses a shared library that is not available.
     */
    public final static int INSTALL_FAILED_MISSING_SHATRED_LIBRARY = -9;
    /**
     * Installation return code
     * <br> the new package uses a shared library that is not available.
     */
    public final static int INSTALL_FAILED_REPLACE_COULDNT_DELETE = -10;
    /**
     * Installation return code
     * <br> the new package failed while optimizing and validating its dex files, either because there was not enough storage
     * or the validation failed.
     */
    public final static int INSTALL_FAILED_DEXOPT = -11;
    /**
     * Installation return code
     * <br> the new package failed because the current SDK version is older than that required by the package.
     */
    public final static int INSTALL_FAILED_OLDER_SDK = -12;
    /**
     * Installation return code
     * <br> the new package failed because it contains a content provider with the same authority as a provider already
     * installed in the system.
     */
    public final static int INSTALL_FAILED_CONFICTING_PROVIDER = -13;
    /**
     * Installation return code
     * <br> the new package failed because the current SDK version is newer than that required by the package.
     */
    public final static int INSTALL_FAILED_NEWER_SDK = -14;
    /**
     * Installation return code<br/>
     * the new package failed because it has specified that it is a test-only package and the caller has not supplied
     * the {#INSTALL_ALLOW_TEST} flag.
     */
    public static final int INSTALL_FAILED_TEST_ONLY = -15;

    /**
     * Installation return code<br/>
     * the package being installed contains native code, but none that is compatible with the the device's CPU_ABI.
     */
    public static final int INSTALL_FAILED_CPU_ABI_INCOMPATIBLE = -16;

    /**
     * Installation return code<br/>
     * the new package uses a feature that is not available.
     */
    public static final int INSTALL_FAILED_MISSING_FEATURE = -17;

    /**
     * Installation return code<br/>
     * a secure container mount point couldn't be accessed on external media.
     */
    public static final int INSTALL_FAILED_CONTAINER_ERROR = -18;

    /**
     * Installation return code<br/>
     * the new package couldn't be installed in the specified install location.
     */
    public static final int INSTALL_FAILED_INVALID_INSTALL_LOCATION = -19;

    /**
     * Installation return code<br/>
     * the new package couldn't be installed in the specified install location because the media is not available.
     */
    public static final int INSTALL_FAILED_MEDIA_UNAVAILABLE = -20;

    /**
     * Installation return code<br/>
     * the new package couldn't be installed because the verification timed out.
     */
    public static final int INSTALL_FAILED_VERIFICATION_TIMEOUT = -21;

    /**
     * Installation return code<br/>
     * the new package couldn't be installed because the verification did not succeed.
     */
    public static final int INSTALL_FAILED_VERIFICATION_FAILURE = -22;

    /**
     * Installation return code<br/>
     * the package changed from what the calling program expected.
     */
    public static final int INSTALL_FAILED_PACKAGE_CHANGED = -23;

    /**
     * Installation return code<br/>
     * the new package is assigned a different UID than it previously held.
     */
    public static final int INSTALL_FAILED_UID_CHANGED = -24;

    /**
     * Installation return code<br/>
     * if the parser was given a path that is not a file, or does not end with the expected '.apk' extension.
     */
    public static final int INSTALL_PARSE_FAILED_NOT_APK = -100;

    /**
     * Installation return code<br/>
     * if the parser was unable to retrieve the AndroidManifest.xml file.
     */
    public static final int INSTALL_PARSE_FAILED_BAD_MANIFEST = -101;

    /**
     * Installation return code<br/>
     * if the parser encountered an unexpected exception.
     */
    public static final int INSTALL_PARSE_FAILED_UNEXPECTED_EXCEPTION = -102;

    /**
     * Installation return code<br/>
     * if the parser did not find any certificates in the .apk.
     */
    public static final int INSTALL_PARSE_FAILED_NO_CERTIFICATES = -103;

    /**
     * Installation return code<br/>
     * if the parser found inconsistent certificates on the files in the .apk.
     */
    public static final int INSTALL_PARSE_FAILED_INCONSISTENT_CERTIFICATES = -104;

    /**
     * Installation return code<br/>
     * if the parser encountered a CertificateEncodingException in one of the files in the .apk.
     */
    public static final int INSTALL_PARSE_FAILED_CERTIFICATE_ENCODING = -105;

    /**
     * Installation return code<br/>
     * if the parser encountered a bad or missing package name in the manifest.
     */
    public static final int INSTALL_PARSE_FAILED_BAD_PACKAGE_NAME = -106;

    /**
     * Installation return code<br/>
     * if the parser encountered a bad shared user id name in the manifest.
     */
    public static final int INSTALL_PARSE_FAILED_BAD_SHARED_USER_ID = -107;

    /**
     * Installation return code<br/>
     * if the parser encountered some structural problem in the manifest.
     */
    public static final int INSTALL_PARSE_FAILED_MANIFEST_MALFORMED = -108;

    /**
     * Installation return code<br/>
     * if the parser did not find any actionable tags (instrumentation or application) in the manifest.
     */
    public static final int INSTALL_PARSE_FAILED_MANIFEST_EMPTY = -109;

    /**
     * Installation return code<br/>
     * if the system failed to install the package because of system issues.
     */
    public static final int INSTALL_FAILED_INTERNAL_ERROR = -110;

    public static final int INSTALL_FAILED_DUPLICATE_PERMISSION = -112;
    /**
     * Installation return code<br/>
     * other reason
     */
    public static final int INSTALL_FAILED_OTHER = -1000000;

    public static String getFingerprintSha1(Context context, String packageName) {
        X509Certificate cf = getPackageX509Certificate(context, packageName);
        if (null == cf) {
            return null;
        }
        String hex = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] publicKey = md.digest(cf.getEncoded());
            hex = byte2HexFormatted(publicKey);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (CertificateEncodingException e) {
            e.printStackTrace();
        }
        return hex;
    }

    public static String getFingerprintMd5(Context context, String packageName) {
        X509Certificate cf = getPackageX509Certificate(context, packageName);
        if (null == cf) {
            return null;
        }
        String hex = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] publicKey = md.digest(cf.getEncoded());
            hex = byte2HexFormatted(publicKey);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (CertificateEncodingException e) {
            e.printStackTrace();
        }
        return hex;
    }

    private static String byte2HexFormatted(byte[] bytes) {
        final char[] HEX = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'A', 'B', 'C', 'D', 'E', 'F'};
        char[] hexChars = new char[bytes.length * 3 - 1];
        int v = 0x00;
        for (int i = 0; i < bytes.length; i++) {
            v = bytes[i] & 0xff; // 保留最后两位，即两个16进制位
            // high 4bit
            hexChars[i * 3] = HEX[v >>> 4]; // 忽略符号右移，空出补0
            // low 4bit
            hexChars[i * 3 + 1] = HEX[v & 0x0f];
            if (i < bytes.length - 1) {
                hexChars[i * 3 + 2] = ':';
            }
        }
        return String.valueOf(hexChars);
    }

    public static X509Certificate getPackageX509Certificate(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        int flags = PackageManager.GET_SIGNATURES;
        PackageInfo packageInfo = null;
        try {
            packageInfo = pm.getPackageInfo(packageName, flags);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }

        Signature[] signatures = packageInfo.signatures;
        if (null == signatures || signatures.length < 1) {
            return null;
        }
        byte[] cert = signatures[0].toByteArray();
        InputStream input = new ByteArrayInputStream(cert);
        CertificateFactory cf = null;
        try {
            cf = CertificateFactory.getInstance("X509");
        } catch (CertificateException e) {
            e.printStackTrace();
            return null;
        }
        X509Certificate c = null;
        try {
            c = (X509Certificate) cf.generateCertificate(input);
        } catch (CertificateException e) {
            e.printStackTrace();
            return null;
        }
        try {
            input.close();
            input = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return c;
    }

    public static int getPackageUid(Context context, String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            return -1;
        }

        int uid = -1;
        PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> applications = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        ApplicationInfo a = null;
        if (null != applications && applications.size() > 0) {
            for (int i = 0; i < applications.size(); i++) {
                a = applications.get(i);
                if (TextUtils.equals(a.packageName, packageName)) {
                    uid = a.uid;
                }
                a = null;
            }
        }
        return uid;
    }

    public static boolean checkPackageInstall(Context context, String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            return false;
        }
        try {
            PackageManager packageManager = context.getPackageManager();
            packageManager.getApplicationInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (NameNotFoundException e) {
            return false;
        }
    }

    public static PackageInfo getPackageInfo(Context context, String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            return null;
        }
        PackageInfo info = null;
        try {
            PackageManager manager = context.getPackageManager();
            info = manager.getPackageInfo(packageName, 0);
        } catch (NameNotFoundException e) {
        }
        return info;
    }

    public static int getVersionCode(Context context, String packageName) {
        if (packageName == null || "".equals(packageName))
            return -1;
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName,
                    PackageManager.GET_UNINSTALLED_PACKAGES);
            return packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            return -1;
        }
    }

    public static String getVersionName(Context context, String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            return "";
        }

        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName,
                    PackageManager.GET_UNINSTALLED_PACKAGES);
            return packageInfo.versionName;
        } catch (NameNotFoundException e) {
            return "";
        }
    }

    /**
     * 检查apk的Manifest是否注册了某个服务
     *
     * @param context
     * @param packageName 包名，<b>参数不做合法检查</b>
     * @param serviceName 服务名称，<b>参数不做合法检查</b>
     * @return 如果注册了返回true，否则返回false。
     */
    public static boolean isManifestRegisterService(Context context, String packageName,
                                                    String serviceName) {
        try {
            int flags = PackageManager.GET_SERVICES;
            PackageInfo pkinfo = context.getPackageManager().getPackageInfo(packageName,
                    flags);
            if (null == pkinfo || null == pkinfo.services) {
                return false;
            }

            boolean find = false;
            ServiceInfo service;
            final int N = pkinfo.services.length;
            for (int i = 0; i < N; i++) {
                service = pkinfo.services[i];
                if (null != service && TextUtils.equals(serviceName, service.name)) {
                    find = true;
                    break;
                }
            }
            return find;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isSystemApplication(Context context) {
        if (null == context) {
            return false;
        }
        return isSystemApplication(context, context.getPackageName());
    }

    public static boolean isSystemApplication(Context context, String packageName) {
        if (null == context) {
            return false;
        }

        return isSystemApplication(context.getPackageManager(), packageName);
    }

    /**
     * whether packageName is system application which default install on /system/app
     *
     * @param packageManager
     * @param packageName
     * @return
     */
    public static boolean isSystemApplication(PackageManager packageManager, String packageName) {
        if (null == packageManager || TextUtils.isEmpty(packageName)) {
            return false;
        }
        try {
            ApplicationInfo app = packageManager.getApplicationInfo(packageName, 0);
            return (null != app && (app.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * whether the ap whost package's name is packageName is on the top of the stack
     * <ul>
     * <strong>Attentions:<strong/>
     * <li>You should add <strong>android.permission.GET_TASKS</strong> (signature or system app)
     * in manifest</li>
     * </ul>
     *
     * @param context
     * @param packageName
     * @return
     */
    public static boolean isTopActivity(Context context, String packageName) {
        if (null == context || TextUtils.isEmpty(packageName)) {
            return false;
        }

        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> tasksInfo = activityManager.getRunningTasks(1);
        if (null == tasksInfo || tasksInfo.size() < 1) {
            return false;
        }
        try {
            return TextUtils.equals(packageName, tasksInfo.get(0).topActivity.getPackageName());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void uninstallNormal(Context context, String packageName) {
        Uri uri = Uri.fromParts("package", packageName, null);
        Intent intent = new Intent(Intent.ACTION_DELETE, uri);
        try {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            // use silent uninstall.
//		    uninstallSilent(context, packageName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String makeUninstallCommand(String packageName) {
        return String.format("pm uninstall '%s'", packageName);
    }


    public static String packageNameFromFile(Context context, String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            Log.w(TAG, "invalid filePath: " + filePath);
            return "";
        }
        return packageNameFromFile(context, new File(filePath.trim()));
    }

    public static String packageNameFromFile(Context context, File file) {
        if (null == file || !file.exists()) {
            String message = (null == file ? "null" : file.getAbsoluteFile() + " not exist");
            Log.w(TAG, "invalidate file: " + message);
            return "";
        }

        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(file.getAbsolutePath(), 0);
        return null != info ? info.packageName : "";
    }


    public static boolean installNormal(Context context, String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return false;
        }
        return installNormal(context, new File(filePath));
    }

    /**
     * install package normal by system intent
     * 注意：针对android7.0需要在AndroidManifest.xml中配置FileProvider
     *
     * @param context
     * @param apkFile
     * @return return true start system install dialog success. otherwise return false.
     */
    public static boolean installNormal(Context context, File apkFile) {
        if (null == apkFile || !apkFile.exists()) {
            return false;
        }

        if (apkFile.getParent().startsWith(context.getCacheDir().getAbsolutePath())) {
            String[] args1 = {"chmod", "705", apkFile.getParent()};
            ShellUtil.execCommand(args1, false);
        }

        // check app had install or not
        PackageManager pm = context.getPackageManager();
        PackageInfo fileInfo = pm.getPackageArchiveInfo(apkFile.getAbsolutePath(), 0);
        if (null != fileInfo && !TextUtils.isEmpty(fileInfo.packageName)) {
            PackageInfo info = getPackageInfo(context, fileInfo.packageName);
            if (null != info && info.versionCode >= fileInfo.versionCode) {
                Log.i(TAG, "install normal. apk: " + apkFile.getAbsolutePath() + " already install.");
                return true;
            }
        }

        apkFile.setReadable(true, false);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= 24) {
            Uri contentUri = FileProvider.getUriForFile(context,
                    context.getPackageName() + ".file_provider", apkFile);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.parse("file://" + apkFile.getAbsolutePath()),
                    "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        try {
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private static String makeInstallCommand(String filePath) {
        return String.format("pm install -r '%s'", filePath);
    }


    public static int installSilent(Context context, String filePath, boolean isRoot) {
        if (TextUtils.isEmpty(filePath)) {
            return INSTALL_FAILED_INVALID_URI;
        }

        File file = new File(filePath);
        if (null == file || file.length() < 1 || !file.exists() || !file.isFile()) {
            return INSTALL_FAILED_INVALID_URI;
        }
        file.setReadable(true, false);

        /**
         * if context is system app, don't need root permission, but should add
         *  <uses-permission android:name="android.permission.INSTALL_PACKAGES" /> in mainfest
         */
        String command = makeInstallCommand(file.getAbsolutePath());
        ShellUtil.CommandResult commandResult = ShellUtil.execCommand(command, isRoot, true);
        if (null != commandResult.successMsg
                && commandResult.successMsg.toLowerCase(Locale.US).contains("success")) {
            return INSTALL_SUCCEEDED;
        }

        if (null == commandResult.errorMsg) {
            return INSTALL_FAILED_OTHER;
        }
        if (commandResult.containsInstallError("INSTALL_FAILED_ALREADY_EXISTS")) {
            return INSTALL_FAILED_ALREADY_EXISTS;
        }
        if (commandResult.containsInstallError("INSTALL_FAILED_ALREADY_EXISTS")) {
            return INSTALL_FAILED_ALREADY_EXISTS;
        }
        if (commandResult.containsInstallError("INSTALL_FAILED_INVALID_APK")) {
            return INSTALL_FAILED_INVALID_APK;
        }
        if (commandResult.containsInstallError("INSTALL_FAILED_INVALID_URI")) {
            return INSTALL_FAILED_INVALID_URI;
        }
        if (commandResult.containsInstallError("INSTALL_FAILED_INSUFFICIENT_STORAGE")) {
            return INSTALL_FAILED_INSFUFFICIENT_STORAGE;
        }
        if (commandResult.containsInstallError("INSTALL_FAILED_DUPLICATE_PACKAGE")) {
            return INSTALL_FAILED_DUPLICATE_PACKAGE;
        }
        if (commandResult.containsInstallError("INSTALL_FAILED_NO_SHARED_USER")) {
            return INSTALL_FAILED_NOT_SHARED_USER;
        }
        if (commandResult.containsInstallError("INSTALL_FAILED_UPDATE_INCOMPATIBLE")) {
            return INSTALL_FAILED_UPDATE_INCOMPATIBLE;
        }
        if (commandResult.containsInstallError("INSTALL_FAILED_MISSING_SHARED_LIBRARY")) {
            return INSTALL_FAILED_MISSING_SHATRED_LIBRARY;
        }
        if (commandResult.containsInstallError("INSTALL_FAILED_REPLACE_COULDNT_DELETE")) {
            return INSTALL_FAILED_REPLACE_COULDNT_DELETE;
        }
        if (commandResult.containsInstallError("INSTALL_FAILED_DEXOPT")) {
            return INSTALL_FAILED_DEXOPT;
        }
        if (commandResult.containsInstallError("INSTALL_FAILED_OLDER_SDK")) {
            return INSTALL_FAILED_OLDER_SDK;
        }
        if (commandResult.containsInstallError("INSTALL_FAILED_CONFLICTING_PROVIDER")) {
            return INSTALL_FAILED_CONFICTING_PROVIDER;
        }
        if (commandResult.containsInstallError("INSTALL_FAILED_NEWER_SDK")) {
            return INSTALL_FAILED_NEWER_SDK;
        }
        // FUNTODO
        return INSTALL_FAILED_OTHER;
    }

    public static boolean isAppExist(Context context, String pkgName) {
        if (context == null || TextUtils.isEmpty(pkgName)) {
            return false;
        }
        ApplicationInfo info;
        try {
            info = context.getPackageManager().getApplicationInfo(pkgName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            info = null;
        }
        return info != null;
    }
}
