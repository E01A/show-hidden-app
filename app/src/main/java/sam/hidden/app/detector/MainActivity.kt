package sam.hidden.app.detector


import android.app.AlertDialog
import android.content.ComponentName
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity

import java.util.ArrayList

class MainActivity : AppCompatActivity() {

    private var installedApps: List<AppList>? = null
    internal var installedAppAdapter: AppAdapter? = null
    lateinit var userInstalledApps: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        userInstalledApps = findViewById<View>(R.id.installed_app_list) as ListView

        installedApps = getInstalledApps()
        installedAppAdapter = AppAdapter(this@MainActivity, installedApps!!)
        userInstalledApps.adapter = installedAppAdapter
        userInstalledApps.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
            val colors = arrayOf("Click")
            val builder = AlertDialog.Builder(this@MainActivity)
            builder.setTitle("info this app or delete app")
                    .setItems(colors) { dialog, which ->
                        if (which == 0) {
                            val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            intent.data = Uri.parse("package:" + installedApps!![i].packages)
                            startActivity(intent)

                        }
                    }
            builder.show()
        }

        val abc = userInstalledApps.count.toString() + ""
        val countApps = findViewById<View>(R.id.countApps) as TextView
        countApps.text = "Hidden Installed Apps: $abc"

    }

    private fun getInstalledApps(): List<AppList> {
        val pm = packageManager
        val apps = ArrayList<AppList>()
        val packs = packageManager.getInstalledPackages(0)
        for (i in packs.indices) {
            val p = packs[i]
            if (!isSystemPackage(p)) {
                val appName = p.applicationInfo.loadLabel(packageManager).toString()
                val icon = p.applicationInfo.loadIcon(packageManager)
                val packages = p.applicationInfo.packageName
                try {
                    val pi = pm.getPackageInfo(packages, PackageManager.GET_ACTIVITIES)
                    if (pi.activities != null) {
                        val Activities_array = pi.activities
                        for (j in Activities_array.indices) {
                            val componentName = ComponentName(packages, pi.activities[j].name)
                            val res = pm.getComponentEnabledSetting(componentName)
                            if (res == PackageManager.COMPONENT_ENABLED_STATE_DISABLED) {
                                apps.add(AppList("user installed app \n$appName", icon, packages))

                            }
                        }

                    }
                } catch (e: PackageManager.NameNotFoundException) {
                    e.printStackTrace()
                }

            } else if (isSystemPackage(p)) {

                val appName = p.applicationInfo.loadLabel(packageManager).toString()
                val icon = p.applicationInfo.loadIcon(packageManager)
                val packages = p.applicationInfo.packageName
                try {
                    val pi = pm.getPackageInfo(packages, PackageManager.GET_ACTIVITIES)
                    if (pi.activities != null) {
                        val Activities_array = pi.activities
                        for (j in Activities_array.indices) {
                            val componentName = ComponentName(packages, pi.activities[j].name)
                            val res = pm.getComponentEnabledSetting(componentName)
                            if (res == PackageManager.COMPONENT_ENABLED_STATE_DISABLED) {
                                apps.add(AppList("system installed app \n$appName", icon, packages))

                            }
                        }

                    }
                } catch (e: PackageManager.NameNotFoundException) {
                    e.printStackTrace()
                }

            }
        }
        return apps
    }

    private fun isSystemPackage(pkgInfo: PackageInfo): Boolean {
        return pkgInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0
    }

    inner class AppAdapter(context: Context, var listStorage: List<AppList>) : BaseAdapter() {

        var layoutInflater: LayoutInflater

        init {
            layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        }

        override fun getCount(): Int {
            return listStorage.size
        }

        override fun getItem(position: Int): Any {
            return position
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var convertView = convertView

            val listViewHolder: ViewHolder
            if (convertView == null) {
                listViewHolder = ViewHolder()
                convertView = layoutInflater.inflate(R.layout.installed_app_list, parent, false)

                listViewHolder.textInListView = convertView!!.findViewById<View>(R.id.list_app_name) as TextView
                listViewHolder.imageInListView = convertView.findViewById<View>(R.id.app_icon) as ImageView
                listViewHolder.packageInListView = convertView.findViewById<View>(R.id.app_package) as TextView
                convertView.tag = listViewHolder
            } else {
                listViewHolder = convertView.tag as ViewHolder
            }
            listViewHolder.textInListView!!.text = listStorage[position].name
            listViewHolder.imageInListView!!.setImageDrawable(listStorage[position].icon)
            listViewHolder.packageInListView!!.text = listStorage[position].packages
            return convertView
        }

        internal inner class ViewHolder {
            var textInListView: TextView? = null
            var imageInListView: ImageView? = null
            var packageInListView: TextView? = null
        }
    }

    inner class AppList(val name: String, icon: Drawable, val packages: String) {
        var icon: Drawable
            internal set

        init {
            this.icon = icon
        }

    }

}
