import { Handle, Position } from "reactflow";

export const BaseNode = ({ id, data, config }) => {
  const { title, fields = [], handles = [], width = 200, height = 80 } = config;

  return (
    <div style={{ width, height, border: "1px solid black" }}>
      {/* Input Handles */}
      {handles
        .filter((h) => h.type === "target")
        .map((h) => (
          <Handle
            key={h.id}
            type="target"
            position={Position.Left}
            id={`${id}-${h.id}`}
            style={h.style}
          />
        ))}

      {/* Title */}
      <div>
        <span>{title}</span>
      </div>

      {/* Fields */}
      <div>
        {fields.map((field, idx) => (
          <label key={idx}>
            {field.label}:{field.render(data, id)}
          </label>
        ))}
      </div>

      {/* Output Handles */}
      {handles
        .filter((h) => h.type === "source")
        .map((h) => (
          <Handle
            key={h.id}
            type="source"
            position={Position.Right}
            id={`${id}-${h.id}`}
            style={h.style}
          />
        ))}
    </div>
  );
};
