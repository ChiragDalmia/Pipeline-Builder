import { BaseNode } from "./baseNode";

export const MathNode = ({ id, data }) => {
  const config = {
    title: "Math",
    fields: [],
    handles: [
      { type: "target", id: "a", style: { top: "33%" } },
      { type: "target", id: "b", style: { top: "66%" } },
      { type: "source", id: "result" },
    ],
  };

  return <BaseNode id={id} data={data} config={config} />;
};
