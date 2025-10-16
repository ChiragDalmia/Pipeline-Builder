import { BaseNode } from "./baseNode";

export const ConditionalNode = ({ id, data }) => {
  const config = {
    title: "Conditional",
    fields: [],
    handles: [
      { type: "target", id: "condition" },
      { type: "source", id: "true", style: { top: "40%" } },
      { type: "source", id: "false", style: { top: "60%" } },
    ],
  };

  return <BaseNode id={id} data={data} config={config} />;
};
