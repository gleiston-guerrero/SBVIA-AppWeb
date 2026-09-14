import matplotlib
matplotlib.use('Agg')
import matplotlib.pyplot as plt
import matplotlib.patches as patches

def draw_mvc():
    fig, ax = plt.subplots(figsize=(10, 6))
    ax.axis('off')
    
    # Draw boxes
    boxes = [
        {"rect": (0.1, 0.4, 0.2, 0.2), "text": "Angular Client\n(Frontend)"},
        {"rect": (0.4, 0.6, 0.2, 0.2), "text": "Spring Boot\nController\n(Validation)"},
        {"rect": (0.4, 0.2, 0.2, 0.2), "text": "Service Layer\n(Business Logic)"},
        {"rect": (0.7, 0.6, 0.2, 0.2), "text": "Redis 7\n(Hot Cache)"},
        {"rect": (0.7, 0.2, 0.2, 0.2), "text": "PostgreSQL 16\n(Cold Data)"}
    ]
    
    for b in boxes:
        rect = patches.Rectangle((b['rect'][0], b['rect'][1]), b['rect'][2], b['rect'][3],
                               linewidth=2, edgecolor='black', facecolor='#56B4E9')
        ax.add_patch(rect)
        ax.text(b['rect'][0] + 0.1, b['rect'][1] + 0.1, b['text'],
                horizontalalignment='center', verticalalignment='center',
                fontsize=11, fontweight='bold')
                
    # Draw arrows
    ax.annotate('', xy=(0.4, 0.7), xytext=(0.3, 0.5), arrowprops=dict(facecolor='black', shrink=0.05))
    ax.annotate('', xy=(0.4, 0.3), xytext=(0.3, 0.5), arrowprops=dict(facecolor='black', shrink=0.05))
    ax.annotate('', xy=(0.7, 0.7), xytext=(0.6, 0.7), arrowprops=dict(facecolor='black', shrink=0.05))
    ax.annotate('', xy=(0.7, 0.3), xytext=(0.6, 0.3), arrowprops=dict(facecolor='black', shrink=0.05))
    ax.annotate('', xy=(0.5, 0.6), xytext=(0.5, 0.4), arrowprops=dict(facecolor='black', shrink=0.05))
    
    ax.set_title("Hybrid Web Architecture: MVC Data Flow", fontsize=14, fontweight='bold')
    
    fig.savefig('docs/diagramas/flujo-mvc-springboot.png', dpi=120)
    print("Generated docs/diagramas/flujo-mvc-springboot.png")

def draw_dashboard():
    fig, ax = plt.subplots(figsize=(10, 6))
    ax.axis('off')
    
    # Outer frame
    rect = patches.Rectangle((0.05, 0.05), 0.9, 0.9, linewidth=3, edgecolor='black', facecolor='#F0F0F0')
    ax.add_patch(rect)
    
    # Header
    header = patches.Rectangle((0.05, 0.8), 0.9, 0.15, linewidth=2, edgecolor='black', facecolor='#009E73')
    ax.add_patch(header)
    ax.text(0.5, 0.875, "SBVIA Dashboard (Landmark: main)", 
            horizontalalignment='center', verticalalignment='center', fontsize=14, color='white', fontweight='bold')
            
    # Sidebar
    sidebar = patches.Rectangle((0.05, 0.05), 0.25, 0.75, linewidth=2, edgecolor='black', facecolor='#E69F00')
    ax.add_patch(sidebar)
    ax.text(0.175, 0.425, "Navigation Menu\n- Home\n- Scenarios\n- Practice\n- Profile", 
            horizontalalignment='center', verticalalignment='center', fontsize=12)
            
    # Main Content
    main = patches.Rectangle((0.35, 0.1), 0.55, 0.65, linewidth=2, edgecolor='black', facecolor='#FFFFFF')
    ax.add_patch(main)
    ax.text(0.625, 0.425, "Main Content Area\n(Accessible & SEO Optimized)\n\nPerformance: 86/100\nAccessibility: 100/100\nBest Practices: 100/100", 
            horizontalalignment='center', verticalalignment='center', fontsize=12)
            
    fig.savefig('docs/capturas/dashboard.png', dpi=120)
    print("Generated docs/capturas/dashboard.png")

def draw_c4_container():
    fig, ax = plt.subplots(figsize=(10, 6))
    ax.axis('off')
    
    # Outer system boundary
    sys_bound = patches.Rectangle((0.1, 0.1), 0.8, 0.8, fill=False, linestyle='dashed', edgecolor='gray')
    ax.add_patch(sys_bound)
    ax.text(0.12, 0.85, "SBVIA System Boundary", fontsize=12, color='gray')
    
    # Containers
    boxes = [
        {"rect": (0.15, 0.5, 0.2, 0.3), "text": "Web Application\n(Angular 17)\n[Container]\n\nDelivers the single\npage application", "color": "#1168bd"},
        {"rect": (0.45, 0.5, 0.2, 0.3), "text": "API Application\n(Spring Boot 3)\n[Container]\n\nProvides traffic\nsimulation logic", "color": "#1168bd"},
        {"rect": (0.25, 0.15, 0.2, 0.25), "text": "Cache\n(Redis)\n[Container]\n\nStores active\nsession & hot data", "color": "#438dd5"},
        {"rect": (0.55, 0.15, 0.2, 0.25), "text": "Database\n(PostgreSQL)\n[Container]\n\nStores user,\nscenario, metrics", "color": "#438dd5"}
    ]
    
    for b in boxes:
        rect = patches.Rectangle((b['rect'][0], b['rect'][1]), b['rect'][2], b['rect'][3],
                               linewidth=1, edgecolor='black', facecolor=b['color'])
        ax.add_patch(rect)
        ax.text(b['rect'][0] + 0.1, b['rect'][1] + 0.15, b['text'],
                horizontalalignment='center', verticalalignment='center',
                fontsize=10, color='white', fontweight='bold')
                
    # Arrows
    ax.annotate('Makes API calls to', xy=(0.45, 0.65), xytext=(0.35, 0.65), arrowprops=dict(facecolor='black', arrowstyle='->'), fontsize=8, ha='center', va='bottom')
    ax.annotate('Reads/Writes to', xy=(0.55, 0.4), xytext=(0.55, 0.5), arrowprops=dict(facecolor='black', arrowstyle='->'), fontsize=8, ha='right', va='center')
    ax.annotate('Reads/Writes to', xy=(0.45, 0.4), xytext=(0.45, 0.5), arrowprops=dict(facecolor='black', arrowstyle='->'), fontsize=8, ha='left', va='center')
    
    ax.set_title("C4 Model: Level 2 (Container) Diagram", fontsize=14, fontweight='bold')
    fig.savefig('docs/diagramas/c4-level2-en.png', dpi=120)
    print("Generated docs/diagramas/c4-level2-en.png")

def draw_uml_class():
    fig, ax = plt.subplots(figsize=(10, 6))
    ax.axis('off')
    
    boxes = [
        {"rect": (0.1, 0.6, 0.25, 0.3), "text": "<<Entity>>\nUser\n------------------\n+ id: Long\n+ email: String\n+ role: Role\n------------------\n+ login()\n+ getProfile()"},
        {"rect": (0.5, 0.6, 0.25, 0.3), "text": "<<Entity>>\nScenario\n------------------\n+ id: Long\n+ difficulty: int\n+ active: bool\n------------------\n+ start()"},
        {"rect": (0.3, 0.1, 0.25, 0.3), "text": "<<Entity>>\nSimulation\n------------------\n+ id: Long\n+ score: double\n+ timestamp: Date\n------------------\n+ finish()"}
    ]
    
    for b in boxes:
        rect = patches.Rectangle((b['rect'][0], b['rect'][1]), b['rect'][2], b['rect'][3],
                               linewidth=2, edgecolor='black', facecolor='#FFFDD0')
        ax.add_patch(rect)
        ax.text(b['rect'][0] + 0.125, b['rect'][1] + 0.15, b['text'],
                horizontalalignment='center', verticalalignment='center',
                fontsize=10, family='monospace')
                
    # Relations
    ax.annotate('1..* performs', xy=(0.425, 0.4), xytext=(0.225, 0.6), arrowprops=dict(facecolor='black', arrowstyle='-'), fontsize=9, ha='center', va='center')
    ax.annotate('1..* includes', xy=(0.425, 0.4), xytext=(0.625, 0.6), arrowprops=dict(facecolor='black', arrowstyle='-'), fontsize=9, ha='center', va='center')
    
    ax.set_title("UML Class Diagram (Core Domain)", fontsize=14, fontweight='bold')
    fig.savefig('docs/diagramas/uml-classes-en.png', dpi=120)
    print("Generated docs/diagramas/uml-classes-en.png")

if __name__ == '__main__':
    draw_mvc()
    draw_dashboard()
    draw_c4_container()
    draw_uml_class()
