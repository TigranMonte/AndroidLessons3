package ru.tikodvlp.criminalintent

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.text.format.DateFormat
import android.view.*
import android.widget.ImageButton
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import java.util.*

private const val TAG = "CrimeListFragment"

class CrimeListFragment : Fragment() {

    interface Callbacks {
        fun onCrimeSelected(crimeId: UUID);
    }

    private var callbacks: Callbacks? = null

    private val crimeListViewModel: CrimeListViewModel by lazy {
        ViewModelProviders.of(this).get(CrimeListViewModel::class.java)
    }

    private lateinit var crimeRecyclerView: RecyclerView;

    private var adapter: CrimeAdapter? = CrimeAdapter(emptyList())

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_crime_list, container, false)
        crimeRecyclerView = view.findViewById(R.id.crime_recycler_view) as RecyclerView
        crimeRecyclerView.layoutManager = LinearLayoutManager(context)
        crimeRecyclerView.adapter = adapter
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        crimeListViewModel.crimeListLiveData.observe(
            viewLifecycleOwner,
            Observer { crimes ->
                crimes?.let { Log.i(TAG, "Got crimes ${crimes.size}") }
                updateUI(crimes)
            })
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.fragment_crime_list, menu)
            }
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.new_crime -> {
                        val crime = Crime()
                        crimeListViewModel.addCrime(crime)
                        callbacks?.onCrimeSelected(crime.id)
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    private fun updateUI(crimes: List<Crime>) {
        var tvNoData = view?.findViewById(R.id.tvNoData) as TextView
        if (crimes.isEmpty()){
            tvNoData = view?.findViewById(R.id.tvNoData) as TextView
            tvNoData.setVisibility(View.VISIBLE)
        } else {
            tvNoData.setVisibility(View.GONE)
            adapter = CrimeAdapter(crimes)
            crimeRecyclerView.adapter = adapter
        }
    }

    companion object {
        fun newInstance(): CrimeListFragment {
            return CrimeListFragment()
        }
    }
    private inner class CrimeHolder(view: View) : RecyclerView.ViewHolder(view),
        View.OnClickListener {
        private val titleTextView: TextView = itemView.findViewById(R.id.crime_title) as TextView
        private val dateTextView: TextView = itemView.findViewById(R.id.crime_date) as TextView
        private val solvedImageView: ImageView =
            itemView.findViewById(R.id.crime_solved) as ImageView
        private lateinit var crime: Crime

        init {
            itemView.setOnClickListener(this)
        }
        fun bind(crime: Crime) {
            this.crime = crime
            titleTextView.text = crime.title
            dateTextView.text = crime.date.toString()
            solvedImageView.visibility = if (crime.isSolved) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
        override fun onClick(v: View?) {
            callbacks?.onCrimeSelected(crime.id)
        }
    }
    private inner class CrimeAdapter(var crimes: List<Crime>) :
        RecyclerView.Adapter<CrimeHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrimeHolder {
            val view = layoutInflater.inflate(R.layout.list_item_crime, parent, false)
            return CrimeHolder(view)
        }
        override fun onBindViewHolder(holder: CrimeHolder, position: Int) {
            val crime = crimes[position]
            holder.bind(crime)
        }
        override fun getItemCount(): Int = crimes.size
    }
}