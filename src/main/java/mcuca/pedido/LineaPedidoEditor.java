package mcuca.pedido;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.Binder;
import com.vaadin.event.ShortcutAction;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.ValoTheme;

import mcuca.menu.Menu;
import mcuca.menu.MenuRepository;
import mcuca.producto.Producto;
import mcuca.producto.ProductoRepository;
import mcuca.security.VaadinSessionSecurityContextHolderStrategy;

@SuppressWarnings("serial")
@SpringComponent
@UIScope
public class LineaPedidoEditor extends VerticalLayout {
	
	private final LineaPedidoRepository repoLineaPedido;
	private final PedidoRepository repoPedido;
	private final ProductoRepository repoProducto;
	private final MenuRepository repoMenu;

	private LineaPedido lineaPedido;
	
	private double Total;
	
	/* Fields to edit properties in LineaPedido entity */
	Label title = new Label("Nueva Linea de Pedido");
	NativeSelect<String> select = new NativeSelect<>("Menú o producto");
	NativeSelect<Integer> cantidad = new NativeSelect<>("Cantidad");
	NativeSelect<Producto> producto = new NativeSelect<>("Producto");
	NativeSelect<Menu> menu = new NativeSelect<>("Menu");

	/* Action buttons */
	Button guardar = new Button("Guardar");
	Button cancelar = new Button("Cancelar");
	Button borrar = new Button("Borrar");
	CssLayout acciones = new CssLayout(guardar, cancelar, borrar);

	Binder<LineaPedido> binder = new Binder<>(LineaPedido.class);
	
	@Autowired
	public LineaPedidoEditor(LineaPedidoRepository repoLineaPedido, PedidoRepository repoPedido, 
			                 ProductoRepository repoProducto, MenuRepository repoMenu) {
		this.repoLineaPedido = repoLineaPedido;
		this.repoPedido = repoPedido;
		this.repoProducto = repoProducto;
		this.repoMenu = repoMenu;
		cantidad.setItems(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);	
		producto.setItems((Collection<Producto>) this.repoProducto.findAll());
		menu.setItems((Collection<Menu>) this.repoMenu.findAll());
		producto.setVisible(false);
		menu.setVisible(false);
		cantidad.setVisible(false);
		select.setItems("Menú", "Producto");
		addComponents(title, select, cantidad, producto, menu, acciones);		
		
		binder.bindInstanceFields(this);

		// Configure and style components
		setSpacing(true);
		acciones.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
		guardar.setStyleName(ValoTheme.BUTTON_PRIMARY);
		guardar.setClickShortcut(ShortcutAction.KeyCode.ENTER);

		// wire action buttons to guardar, borrar and reset
		//guardar.addClickListener(e -> repoLineaPedido.save(lineaPedido))
		guardar.addClickListener(e -> {
			setTotal(0.0f);
			salvar(e);
		});
		borrar.addClickListener(e -> borrar());
		cancelar.addClickListener(e -> editarLineaPedido(lineaPedido));
		select.addSelectionListener(e -> select(select.getSelectedItem().get()));
		
		setVisible(false);
	}
	
	public void select(String selected)
	{
		cantidad.setVisible(true);
		menu.setVisible(selected == "Menú");
		producto.setVisible(selected == "Producto");
	}
	
	public void borrar()
	{
		Pedido pedido = lineaPedido.getPedido();
		if(lineaPedido.getProducto() != null)
			pedido.setPrecio(pedido.getPrecio() - (lineaPedido.getCantidad() * lineaPedido.getProducto().getPrecio()));
		else
			pedido.setPrecio(pedido.getPrecio() - (lineaPedido.getCantidad() * lineaPedido.getMenu().getPrecio()));
		repoPedido.save(pedido);
		repoLineaPedido.delete(lineaPedido);
	}
	
	public void salvar(ClickEvent e) {
		binder.setBean(lineaPedido);
		Pedido pedido;
		if(lineaPedido.getPedido() == null)
		pedido = repoPedido.findOne(
				(Long)VaadinSessionSecurityContextHolderStrategy.getSession().getAttribute("pedido_id"));
		else
			pedido = lineaPedido.getPedido();
		
		//pedido.setPrecio(Total);
		lineaPedido.setCantidad(cantidad.getValue());
		lineaPedido.setProducto(producto.getValue());
		lineaPedido.setEnCocina(false);
		if(lineaPedido.getProducto() == null){
			//pedido.setPrecio((float)0);
			pedido.setPrecio(pedido.getPrecio() + (lineaPedido.getMenu().getPrecio() * lineaPedido.getCantidad()));
			lineaPedido.setPedido(pedido);
}
		else{
			pedido.setPrecio((pedido.getPrecio() + (lineaPedido.getProducto().getPrecio() * lineaPedido.getCantidad())));
			lineaPedido.setPedido(pedido);
		}
		
		repoLineaPedido.save(lineaPedido);		
		repoPedido.save(pedido);
		
	}
	
	public interface ChangeHandler {

		void onChange();
	}
	
	public final void editarLineaPedido(LineaPedido c) {
		if (c == null) {
			setVisible(false);
			return;
		}
		final boolean persisted = c.getId() != null;
		if (persisted) {
			// Find fresh entity for editing
			lineaPedido = repoLineaPedido.findOne(c.getId());
		}
		else {
			lineaPedido = c;
		}
		cancelar.setVisible(persisted);

		// Bind mcuca properties to similarly named fields
		// Could also use annotation or "manual binding" or programmatically
		// moving values from fields to entities before saving
		binder.setBean(lineaPedido);

		setVisible(true);

		// A hack to ensure the whole form is visible
		guardar.focus();
		// Select all text in numero field automatically
		//cantidad.selectAll();
		if(persisted)
			producto.setSelectedItem(lineaPedido.getProducto());
	}
	
	public void setChangeHandler(ChangeHandler h) {
		// ChangeHandler is notified when either guardar or borrar
		// is clicked
		guardar.addClickListener(e -> h.onChange());
		borrar.addClickListener(e -> h.onChange());
	}
	
	public LineaPedidoRepository getRepoProducto() {
		return repoLineaPedido;
	}

	public double getTotal() {
		return Total;
	}

	public void setTotal(double total) {
		Total = total;
	}
}
