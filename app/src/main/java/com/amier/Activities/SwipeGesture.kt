package com.amier.Activities
import android.R
import android.content.Context
import android.graphics.Canvas
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator


abstract class SwipeGesture(context : Context) : ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

    val deleteColor = ContextCompat.getColor(context,R.color.holo_red_light)
    val archiveColor = ContextCompat.getColor(context,R.color.holo_blue_bright)
    val deleteIcon = R.drawable.ic_delete
    val archiveIcon = R.drawable.ic_dialog_email
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

    }



    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        RecyclerViewSwipeDecorator.Builder(
            c,
            recyclerView,
            viewHolder,
            dX,
            dY,
            actionState,
            isCurrentlyActive
        )
            .addSwipeLeftBackgroundColor(deleteColor)
            .addSwipeLeftActionIcon(deleteIcon)
            .addSwipeRightBackgroundColor(archiveColor)
            .addSwipeRightActionIcon(archiveIcon)
//            .addSwipeLeftLabel("Supprimer l'article")
//            .addSwipeRightLabel("Voir réponses")
            .create()
            .decorate()

        super.onChildDraw(c, recyclerView, viewHolder, dX / 4, dY / 4, actionState, isCurrentlyActive)
    }

}