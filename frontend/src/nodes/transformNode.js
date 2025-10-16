import { BaseNode } from "./baseNode";

export const TransformNode = ({ id, data }) => {
  const config = {
    title: "Transform",
    fields: [],
    handles: [
      { type: "target", id: "data" },
      { type: "source", id: "transformed" },
    ],
  };

  return <BaseNode id={id} data={data} config={config} />;
};
